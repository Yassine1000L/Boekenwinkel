package be.ehb.filali.examen_aa_yassine.services;

import be.ehb.filali.examen_aa_yassine.model.BookInfo;
import be.ehb.filali.examen_aa_yassine.model.Reservation;
import be.ehb.filali.examen_aa_yassine.model.User;
import be.ehb.filali.examen_aa_yassine.repositories.ReservationRepository;
import be.ehb.filali.examen_aa_yassine.repositories.UserRepository;
import be.ehb.filali.examen_aa_yassine.services.CatalogusClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Test de belangrijkste reserveringsacties. */
class ReservationServiceTest {
    private ReservationRepository reservationRepository;
    private UserRepository userRepository;
    private CatalogusClient catalogusClient;
    private ReservationService reservationService;
    private BookInfo book;
    private User user;
    private Reservation reservation;

    @BeforeEach
    public void init() {
        reservationRepository = mock(ReservationRepository.class);
        userRepository = mock(UserRepository.class);
        catalogusClient = mock(CatalogusClient.class);
        reservationService = new ReservationService(reservationRepository, userRepository, catalogusClient);

        book = new BookInfo();
        book.setIsbn("9780000000001");
        book.setTitle("Voorbeeldboek");
        book.setStock(3);
        user = new User("Lezer", "lezer@example.com");
        user.setId(1);
        reservation = new Reservation(user, book.getIsbn(), book.getTitle(), 2);
        reservation.setId(1);
    }

    @Test
    public void vraagtVoorraadVerlagingBijReserveringAan() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(catalogusClient.reserveStock(book.getIsbn(), 2)).thenReturn(Optional.of(book));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        Optional<Reservation> result = reservationService.createReservation(1, book.getIsbn(), 2);

        assertTrue(result.isPresent());
        assertEquals(3, book.getStock());
        verify(catalogusClient).reserveStock(book.getIsbn(), 2);
    }

    @Test
    public void verlaagtVoorraadNietOpnieuwBijBevestiging() {
        reservation.setConfirmed(true);
        when(reservationRepository.findById(1)).thenReturn(Optional.of(reservation));

        Optional<Reservation> result = reservationService.confirmReservation(1);

        assertTrue(result.isPresent());
        verify(catalogusClient, never()).reserveStock(book.getIsbn(), 2);
        verify(userRepository, never()).save(user);
    }
}
