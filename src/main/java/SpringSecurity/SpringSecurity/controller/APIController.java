package SpringSecurity.SpringSecurity.controller;

import static SpringSecurity.SpringSecurity.SpringSecurityApplication.SECRET_KEY;

import SpringSecurity.SpringSecurity.model.Book;
import SpringSecurity.SpringSecurity.model.User;
import SpringSecurity.SpringSecurity.service.BookService;
import SpringSecurity.SpringSecurity.service.UserService;
import io.github.bucket4j.Bucket;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class APIController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private Bucket bucket;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) {

        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded for /api/login endpoint");
        }

        if (userService.authenticateUser(username, password)) {
            String token = generateToken(username);
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 7);

            response.addCookie(cookie);
            return ResponseEntity.ok().body("Auth Success");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }

    @PostMapping("/user/add")
    public ResponseEntity<String> addUserSubmit(@RequestParam String username, @RequestParam String password) {
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded for /api/user/add endpoint");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        userService.saveUser(user);
        return ResponseEntity.ok("Created user " + username);
    }

    @PostMapping("/book/add")
    public ResponseEntity<String> addBookSubmit(HttpServletRequest request, @RequestParam String title, @RequestParam String author, @RequestParam int year) {

        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded for /api/book/add endpoint");
        }

        String jwtToken = extractTokenFromCookie(request.getCookies());

        if (jwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Missing or invalid token");
        }

        try {
            String jwtUsername = getUsernameFromToken(jwtToken);

            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(author);
            book.setYear(year);
            book.setOwner(userService.getUserByUsername(jwtUsername));
            bookService.saveBook(book);

            return ResponseEntity.ok("Added book " + book.getTitle());
        } catch (ExpiredJwtException | SignatureException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token");
        }
    }

    @PostMapping("/book/remove")
    public ResponseEntity<String> removeBook(HttpServletRequest request, @RequestParam Long bookId) {
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded for /api/book/remove endpoint");
        }

        String jwtToken = extractTokenFromCookie(request.getCookies());

        if (jwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Missing or invalid token");
        }

        try {
            String jwtUsername = getUsernameFromToken(jwtToken);
            Book book = bookService.getBookById(bookId);

            if (book == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
            }

            if (!jwtUsername.equals(book.getOwner().getUsername())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden: Token does not match book owner");
            }

            bookService.removeBook(book);
            return ResponseEntity.ok("Removed book " + book.getTitle());
        } catch (ExpiredJwtException | SignatureException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token");
        }
    }

    @GetMapping("/book/list")
    public ResponseEntity<List<Book>> getBooksByUsername(HttpServletRequest request) {
        String jwtToken = extractTokenFromCookie(request.getCookies());

        if (jwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }

        try {
            String jwtUsername = getUsernameFromToken(jwtToken);
            List<Book> userBooks = bookService.getBooksByUsername(jwtUsername);
            return ResponseEntity.ok(userBooks);
        } catch (ExpiredJwtException | SignatureException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }
    }


    private String generateToken(String username) {
        return Jwts.builder().setSubject(username).signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes()).compact();
    }

    private String getUsernameFromToken(String jwtToken) {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).build().parseClaimsJws(jwtToken);
        return claimsJws.getBody().getSubject();
    }

    private String extractTokenFromCookie(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
