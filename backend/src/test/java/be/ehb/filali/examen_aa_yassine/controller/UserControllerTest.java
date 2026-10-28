package be.ehb.filali.examen_aa_yassine.controller;

import be.ehb.filali.examen_aa_yassine.model.User;
import be.ehb.filali.examen_aa_yassine.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Test registratie en inloggen van gebruikers. */
class UserControllerTest {
    private UserRepository userRepository;
    private UserController userController;
    private User user;

    @BeforeEach
    public void init() {
        userRepository = mock(UserRepository.class);
        userController = new UserController(userRepository);
        user = new User("Lezer", "lezer@example.com", "voorbeeldwachtwoord");
        user.setId(1);
    }

    @Test
    public void registreertNieuweGebruiker() {
        when(userRepository.findByEmail("lezer@example.com")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(user);

        var response = userController.register("Lezer", "lezer@example.com", "voorbeeldwachtwoord");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void logtInMetJuisteGegevens() {
        when(userRepository.findByEmail("lezer@example.com")).thenReturn(user);

        var response = userController.login("lezer@example.com", "voorbeeldwachtwoord");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    public void weigertFoutWachtwoord() {
        when(userRepository.findByEmail("lezer@example.com")).thenReturn(user);

        var response = userController.login("lezer@example.com", "fout");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
