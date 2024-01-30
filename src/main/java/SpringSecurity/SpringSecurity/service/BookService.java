package SpringSecurity.SpringSecurity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import SpringSecurity.SpringSecurity.model.Book;
import SpringSecurity.SpringSecurity.repository.BookRepository;

import java.util.List;

@Service
public class BookService {


    private BookRepository bookRepository;

    BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public void removeBook(Book book) {
        bookRepository.delete(book);
    }

    public List<Book> getBooksByUsername(String username) {
        return bookRepository.findByOwnerUsername(username);
    }

    public Book getBookById(Long bookId) {
        return bookRepository.findById(bookId).orElse(null);
    }

}
