package be.ehb.filali.examen_aa_yassine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

/** Bewaart een boekreservering en de status van de afhandeling. */
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "book_isbn")
    private String bookIsbn;

    @Column(name = "book_title")
    private String bookTitle;

    @Min(value = 1)
    private int quantity;

    private boolean confirmed;
    private boolean ready;
    private boolean rejected;

    private LocalDate pickupDate;

    public Reservation() {
    }

    public Reservation(User user, String bookIsbn, String bookTitle, int quantity) {
        this.user = user;
        this.bookIsbn = bookIsbn;
        this.bookTitle = bookTitle;
        this.quantity = quantity;
        this.confirmed = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @JsonIgnore
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getBookIsbn() { return bookIsbn; }
    public void setBookIsbn(String bookIsbn) { this.bookIsbn = bookIsbn; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }


    //laat gewoon de aantallen van het aantal terug geven
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public boolean isConfirmed() { return confirmed; }
    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }
    public boolean isReady() { return ready; }
    public void setReady(boolean ready) { this.ready = ready; }
    public boolean isRejected() { return rejected; }
    public void setRejected(boolean rejected) { this.rejected = rejected; }
    public LocalDate getPickupDate() { return pickupDate; }
    public void setPickupDate(LocalDate pickupDate) { this.pickupDate = pickupDate; }

    public String getUserName() { return user == null ? "" : user.getName(); }
    public String getUserEmail() { return user == null ? "" : user.getEmail(); }
}
