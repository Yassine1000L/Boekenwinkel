package be.ehb.filali.examen_aa_yassine.controller;

import be.ehb.filali.examen_aa_yassine.model.Review;
import be.ehb.filali.examen_aa_yassine.model.User;
import be.ehb.filali.examen_aa_yassine.repositories.ReviewRepository;
import be.ehb.filali.examen_aa_yassine.repositories.UserRepository;
import be.ehb.filali.examen_aa_yassine.services.CatalogusClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


//functie dat we hebben gezien in de les met David (rest controller)

/** Biedt REST-aanvragen om boekreviews te bekijken en te plaatsen. */
@RestController
@RequestMapping(value = "/reviews")
public class ReviewController {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CatalogusClient catalogusClient;

    @Autowired
    public ReviewController(ReviewRepository reviewRepository,
                            UserRepository userRepository,
                            CatalogusClient catalogusClient) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.catalogusClient = catalogusClient;
    }



    @GetMapping
    public Iterable<Review> getAllReviews(@RequestParam(required = false) String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return reviewRepository.findAll();
        }
        return reviewRepository.findByIsbn(isbn);
    }

    @PostMapping
    public ResponseEntity<Review> addReview(@RequestParam int userId,
                                            @RequestParam String isbn,
                                            @RequestParam int score,
                                            @RequestParam String description) {
        Optional<User> foundUser = userRepository.findById(userId);
        if (foundUser.isEmpty() || catalogusClient.getBook(isbn).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (score < 0 || score > 5 || description.length() < 3 || description.length() > 180) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!foundUser.get().getPurchasedBookIsbns().contains(isbn)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Review review = new Review(score, description, isbn, foundUser.get());
        return new ResponseEntity<>(reviewRepository.save(review), HttpStatus.CREATED);
    }
}
