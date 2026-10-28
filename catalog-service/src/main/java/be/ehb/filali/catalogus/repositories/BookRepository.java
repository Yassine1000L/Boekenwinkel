package be.ehb.filali.catalogus.repositories;

import be.ehb.filali.catalogus.model.Book;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/** Haalt boeken op en bewaart ze in de database. */
public interface BookRepository extends CrudRepository<Book, String> {
    List<Book> findByTitleContainsIgnoreCase(String title);
}
