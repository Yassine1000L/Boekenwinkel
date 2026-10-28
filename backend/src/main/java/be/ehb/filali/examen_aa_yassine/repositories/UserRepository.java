package be.ehb.filali.examen_aa_yassine.repositories;

import be.ehb.filali.examen_aa_yassine.model.User;
import org.springframework.data.repository.CrudRepository;

/** Haalt klantprofielen op en bewaart ze in de database. */
public interface UserRepository extends CrudRepository<User, Integer> { User findByEmail(String email);
}
