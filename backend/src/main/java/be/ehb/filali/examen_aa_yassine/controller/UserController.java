package be.ehb.filali.examen_aa_yassine.controller;

import be.ehb.filali.examen_aa_yassine.model.User;
import be.ehb.filali.examen_aa_yassine.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/** Beheert registratie, inloggen en gebruikersprofielen via REST. */
@RestController
@RequestMapping(value = "/users")
public class UserController {
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestParam String name,
                                         @RequestParam String email,
                                         @RequestParam String password) {
        if (name.isBlank() || email.isBlank() || password.isBlank()
                || userRepository.findByEmail(email) != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User user = new User(name, email, password);
        return new ResponseEntity<>(userRepository.save(user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestParam String email, @RequestParam String password) {
        User user = userRepository.findByEmail(email);
        if (user == null || !user.getPassword().equals(password)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/admin/login")
    public ResponseEntity<Void> adminLogin(@RequestParam String email, @RequestParam String password) {
        if (!"admin@boek.com".equals(email) || !"test1234".equals(password)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
