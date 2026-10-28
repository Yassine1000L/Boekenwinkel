package be.ehb.filali.examen_aa_yassine.controller;

import be.ehb.filali.examen_aa_yassine.model.Reservation;
import be.ehb.filali.examen_aa_yassine.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/** Beheert reserveringen en de acties van de admin via REST. */
@RestController
@RequestMapping(value = "/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<Iterable<Reservation>> getAllReservations(
            @RequestHeader(value = "X-Admin-Email", required = false) String email,
            @RequestHeader(value = "X-Admin-Password", required = false) String password) {
        if (!isAdmin(email, password)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @PostMapping
    public ResponseEntity<Reservation> addReservation(@RequestParam int userId,
                                                       @RequestParam String isbn,
                                                       @RequestParam int quantity) {
        Optional<Reservation> reservation = reservationService.createReservation(userId, isbn, quantity);
        return reservation.map(value -> new ResponseEntity<>(value, HttpStatus.CREATED))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Reservation> confirmReservation(@PathVariable int id) {
        Optional<Reservation> reservation = reservationService.confirmReservation(id);
        return reservation.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation(
            @PathVariable int id,
            @RequestHeader(value = "X-Admin-Email", required = false) String email,
            @RequestHeader(value = "X-Admin-Password", required = false) String password) {
        if (!isAdmin(email, password)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Optional<Reservation> reservation = reservationService.approveReservation(id);
        return reservation.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Reservation> rejectReservation(
            @PathVariable int id,
            @RequestHeader(value = "X-Admin-Email", required = false) String email,
            @RequestHeader(value = "X-Admin-Password", required = false) String password) {
        if (!isAdmin(email, password)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Optional<Reservation> reservation = reservationService.rejectReservation(id);
        return reservation.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    private boolean isAdmin(String email, String password) {
        return "admin@boek.com".equals(email) && "test1234".equals(password);
    }
}
