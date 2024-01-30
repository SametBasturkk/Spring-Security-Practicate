package SpringSecurity.SpringSecurity.repository;

import SpringSecurity.SpringSecurity.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByOwnerUsername(String username);

}
