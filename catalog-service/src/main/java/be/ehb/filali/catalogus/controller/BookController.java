package be.ehb.filali.catalogus.controller;

import be.ehb.filali.catalogus.model.Book;
import be.ehb.filali.catalogus.repositories.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/** Biedt REST-aanvragen voor boeken en voorraad aan. */
@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "http://localhost:8080")
public class BookController {
    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public Iterable<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<Book> getBook(@PathVariable String isbn) {
        Optional<Book> book = bookRepository.findById(isbn);
        return book.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/search")
    public Iterable<Book> searchBooks(@RequestParam String title) {
        return bookRepository.findByTitleContainsIgnoreCase(title);
    }

    @PostMapping
    public ResponseEntity<Book> addBook(@RequestParam String isbn,
                                        @RequestParam String title,
                                        @RequestParam String authors,
                                        @RequestParam String description,
                                        @RequestParam float price,
                                        @RequestParam int stock) {
        if (isbn.isBlank() || title.isBlank() || price < 0 || stock < 0 || bookRepository.existsById(isbn)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Book book = new Book(isbn, title, authors, description, price, stock);
        return new ResponseEntity<>(bookRepository.save(book), HttpStatus.CREATED);
    }

    @PutMapping("/{isbn}/stock")
    public ResponseEntity<Book> updateStock(@PathVariable String isbn, @RequestParam int stock) {
        Optional<Book> foundBook = bookRepository.findById(isbn);
        if (foundBook.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (stock < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Book book = foundBook.get();
        book.setStock(stock);
        return ResponseEntity.ok(bookRepository.save(book));
    }

    @PutMapping("/{isbn}/reserve")
    public ResponseEntity<Book> reserveStock(@PathVariable String isbn, @RequestParam int quantity) {
        Optional<Book> foundBook = bookRepository.findById(isbn);
        if (foundBook.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Book book = foundBook.get();
        if (quantity < 1 || quantity > book.getStock()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        book.setStock(book.getStock() - quantity);
        return ResponseEntity.ok(bookRepository.save(book));
    }

    @PutMapping("/{isbn}/release")
    public ResponseEntity<Book> releaseStock(@PathVariable String isbn, @RequestParam int quantity) {
        Optional<Book> foundBook = bookRepository.findById(isbn);
        if (foundBook.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (quantity < 1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Book book = foundBook.get();
        book.setStock(book.getStock() + quantity);
        return ResponseEntity.ok(bookRepository.save(book));
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
        if (!bookRepository.existsById(isbn)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        bookRepository.deleteById(isbn);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
