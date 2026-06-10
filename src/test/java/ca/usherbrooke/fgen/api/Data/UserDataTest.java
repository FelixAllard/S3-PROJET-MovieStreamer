package ca.usherbrooke.fgen.api.Data;

import ca.usherbrooke.fgen.api.DAO.MovieRepository;
import ca.usherbrooke.fgen.api.DAO.UserRepository;
import ca.usherbrooke.fgen.api.DAO.WatchMovieUserRepository;
import ca.usherbrooke.fgen.api.Entities.Movie;
import ca.usherbrooke.fgen.api.Entities.User;
import ca.usherbrooke.fgen.api.Entities.WatchMovieUser;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserDataTest {

    private UserRepository userRepository;
    private MovieRepository movieRepository;
    private WatchMovieUserRepository watchMovieUserRepository;
    private UserData userData;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        movieRepository = Mockito.mock(MovieRepository.class);
        watchMovieUserRepository = Mockito.mock(WatchMovieUserRepository.class);
        userData = new UserData(userRepository, movieRepository, watchMovieUserRepository);
    }

    @Test
    void getAllUsers_delegueAuRepositoryEtRetourneListe() {
        User u1 = new User();
        User u2 = new User();
        when(userRepository.listAll()).thenReturn(List.of(u1, u2));

        List<User> result = userData.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository, times(1)).listAll();
    }

    @Test
    void getAllUsers_retourneListeVideSiRepositoryVide() {
        when(userRepository.listAll()).thenReturn(List.of());

        List<User> result = userData.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).listAll();
    }
    @Test
    void getUserByUserId_delegueAuRepositoryEtRetourneUtilisateur() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(user);

        User result = userData.getUserByUserId(1L);

        assertEquals(user, result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserByUserId_retourneNull_siIdInexistant() {
        when(userRepository.findById(99L)).thenReturn(null);

        User result = userData.getUserByUserId(99L);

        assertNull(result);
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void postRatingByUserAndMovieID_ajouteRatingSurWMUExistant() {
        // Arrange
        Movie movie = new Movie();
        movie.id = 1L;

        WatchMovieUser wmu = new WatchMovieUser();
        wmu.setMovie(movie);
        wmu.setRating(null);

        User user = new User();
        user.id = 1L;
        user.watchedMovieUsers = List.of(wmu);

        when(userRepository.findById(1L)).thenReturn(user);

        // Act
        WatchMovieUser result = userData.postRatingByUserAndMovieID(1L, 1L, 4);

        // Assert
        assertEquals(4, result.getRating());
        verify(userRepository, times(1)).findById(1L);
        verify(watchMovieUserRepository, never()).persist(any(WatchMovieUser.class));
    }
    @Test
    void postRatingByUserAndMovieID_creeWMUsiInexistant() {
        // Arrange
        Movie movie = new Movie();
        movie.id = 1L;

        User user = new User();
        user.id = 1L;
        user.watchedMovieUsers = new ArrayList<>(); // liste vide

        when(userRepository.findById(1L)).thenReturn(user);
        when(movieRepository.findById(1L)).thenReturn(movie);

        // Act
        WatchMovieUser result = userData.postRatingByUserAndMovieID(1L, 1L, 4);

        // Assert
        assertEquals(4, result.getRating());
        verify(watchMovieUserRepository, times(1)).persist(any(WatchMovieUser.class));
    }

    @Test
    void postRatingByUserAndMovieID_retourne404SiUserInexistant() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(null);

        // Act & Assert
        WebApplicationException exception = assertThrows(WebApplicationException.class, () ->
                userData.postRatingByUserAndMovieID(99L, 10L, 4)
        );
        assertEquals(404, exception.getResponse().getStatus());
    }

    @Test
    void postRatingByUserAndMovieID_retourne404SiFilmInexistant() {
        // Arrange
        User user = new User();
        user.id = 5L;
        user.watchedMovieUsers = new ArrayList<>();

        when(userRepository.findById(5L)).thenReturn(user);
        when(movieRepository.findById(99L)).thenReturn(null);

        // Act & Assert
        WebApplicationException exception = assertThrows(WebApplicationException.class, () ->
                userData.postRatingByUserAndMovieID(5L, 99L, 4)
        );
        assertEquals(404, exception.getResponse().getStatus());
    }

    @Test
    void postRatingByUserAndMovieID_retourne409SiRatingExistant() {
        // Arrange
        Movie movie = new Movie();
        movie.id = 10L;

        WatchMovieUser wmu = new WatchMovieUser();
        wmu.setMovie(movie);
        wmu.setRating(3); // ← rating déjà existant

        User user = new User();
        user.id = 5L;
        user.watchedMovieUsers = List.of(wmu);

        when(userRepository.findById(5L)).thenReturn(user);

        // Act & Assert
        WebApplicationException exception = assertThrows(WebApplicationException.class, () ->
                userData.postRatingByUserAndMovieID(5L, 10L, 4)
        );
        assertEquals(409, exception.getResponse().getStatus());
    }
}