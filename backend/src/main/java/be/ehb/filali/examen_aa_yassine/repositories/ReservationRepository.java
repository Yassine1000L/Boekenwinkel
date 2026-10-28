package be.ehb.filali.examen_aa_yassine.repositories;

import be.ehb.filali.examen_aa_yassine.model.Reservation;
import org.springframework.data.repository.CrudRepository;

/** Haalt reserveringen op en bewaart ze in de database. */
public interface ReservationRepository extends CrudRepository<Reservation, Integer> {
}
