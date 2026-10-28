package be.ehb.filali.examen_aa_yassine.repositories;

import be.ehb.filali.examen_aa_yassine.model.Review;
import org.springframework.data.repository.CrudRepository;

/** Haalt reviews op en bewaart ze in de database. */
public interface ReviewRepository extends CrudRepository<Review, Integer> {
    Iterable<Review> findByIsbn(String isbn);
}
