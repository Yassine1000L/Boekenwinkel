package be.ehb.filali.examen_aa_yassine.services;

import be.ehb.filali.examen_aa_yassine.model.BookInfo;
import be.ehb.filali.examen_aa_yassine.model.Reservation;
import be.ehb.filali.examen_aa_yassine.model.User;
import be.ehb.filali.examen_aa_yassine.repositories.ReservationRepository;
import be.ehb.filali.examen_aa_yassine.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

/** Verwerkt reserveringen en werkt de voorraad via de catalogusservice bij. */
@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final CatalogusClient catalogusClient;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository,
                              UserRepository userRepository,
                              CatalogusClient catalogusClient) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.catalogusClient = catalogusClient;
    }

    public Iterable<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> createReservation(int userId, String isbn, int quantity) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty() || quantity < 1) {
            return Optional.empty();
        }

        Optional<BookInfo> book = catalogusClient.reserveStock(isbn, quantity);
        if (book.isEmpty()) {
            return Optional.empty();
        }

        Reservation reservation = new Reservation(user.get(), book.get().getIsbn(), book.get().getTitle(), quantity);
        try {
            return Optional.of(reservationRepository.save(reservation));
        } catch (RuntimeException fout) {
            catalogusClient.releaseStock(isbn, quantity);
            throw fout;
        }
    }

    public Optional<Reservation> approveReservation(int reservationId) {
        Optional<Reservation> foundReservation = reservationRepository.findById(reservationId);
        if (foundReservation.isEmpty()) {
            return Optional.empty();
        }

        Reservation reservation = foundReservation.get();
        if (reservation.isRejected() || reservation.isReady()) {
            return Optional.empty();
        }
        reservation.setReady(true);
        reservation.setPickupDate(LocalDate.now().plusDays(1));
        User user = reservation.getUser();
        user.getPurchasedBookIsbns().add(reservation.getBookIsbn());
        userRepository.save(user);
        return Optional.of(reservationRepository.save(reservation));
    }

    public Optional<Reservation> rejectReservation(int reservationId) {
        Optional<Reservation> foundReservation = reservationRepository.findById(reservationId);
        if (foundReservation.isEmpty()) {
            return Optional.empty();
        }

        Reservation reservation = foundReservation.get();
        if (reservation.isConfirmed() || reservation.isReady()) {
            return Optional.empty();
        }
        if (reservation.isRejected()) {
            return Optional.of(reservation);
        }

        if (!catalogusClient.releaseStock(reservation.getBookIsbn(), reservation.getQuantity())) {
            return Optional.empty();
        }
        reservation.setReady(false);
        reservation.setRejected(true);
        return Optional.of(reservationRepository.save(reservation));
    }

    public Optional<Reservation> confirmReservation(int reservationId) {
        Optional<Reservation> foundReservation = reservationRepository.findById(reservationId);
        if (foundReservation.isEmpty()) {
            return Optional.empty();
        }

        Reservation reservation = foundReservation.get();
        if (reservation.isConfirmed()) {
            return Optional.of(reservation);
        }
        if (!reservation.isReady() || reservation.isRejected()) {
            return Optional.empty();
        }

        User user = reservation.getUser();
        user.getPurchasedBookIsbns().add(reservation.getBookIsbn());
        userRepository.save(user);

        reservation.setConfirmed(true);
        return Optional.of(reservationRepository.save(reservation));
    }
}
