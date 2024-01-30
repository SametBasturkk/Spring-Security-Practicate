package SpringSecurity.SpringSecurity.service;

import SpringSecurity.SpringSecurity.model.User;
import SpringSecurity.SpringSecurity.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserService {

    private UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public void saveUser(User user) {
        if (isValidUsername(user.getUsername()) && isValidPassword(user.getPassword())) {
            user.setPassword(passwordEncoder().encode(user.getPassword()));
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }

    private boolean isValidUsername(String username) {
        return username != null && username.length() >= 3 && username.length() <= 50;
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6 && password.length() <= 50
                && containsUppercase(password) && containsLowercase(password)
                && containsDigit(password) && containsSpecialChar(password);
    }

    private boolean containsUppercase(String password) {
        return !password.equals(password.toLowerCase());
    }

    private boolean containsLowercase(String password) {
        return !password.equals(password.toUpperCase());
    }

    private boolean containsDigit(String password) {
        return password.matches(".*\\d.*");
    }

    private boolean containsSpecialChar(String password) {
        Pattern specialCharPattern = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]");
        return specialCharPattern.matcher(password).find();
    }

    public boolean authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username).orElse(null);

        if (user != null && passwordEncoder().matches(password, user.getPassword())) {
            return true;
        } else {
            return false;
        }
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
