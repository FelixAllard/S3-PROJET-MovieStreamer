package ca.usherbrooke.fgen.api.Data;

import ca.usherbrooke.fgen.api.DAO.MovieRepository;
import ca.usherbrooke.fgen.api.DAO.UserRepository;
import ca.usherbrooke.fgen.api.Entities.Movie;
import ca.usherbrooke.fgen.api.Entities.Tag;
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
    private UserData userData;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        movieRepository = Mockito.mock(MovieRepository.class);
        userData = new UserData(userRepository, movieRepository);
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
    void deleteUserByUserId_delegueAuRepositoryEtRetourneTrue() {
        when(userRepository.deleteById(1L)).thenReturn(true);

        boolean result = userData.deleteUserByUserId(1L);

        assertTrue(result);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUserByUserId_retourneFalseSiRepositoryNeSupprimeRien() {
        when(userRepository.deleteById(99L)).thenReturn(false);

        boolean result = userData.deleteUserByUserId(99L);

        assertFalse(result);
        verify(userRepository, times(1)).deleteById(99L);
    }
    
    void updateUserRatingByUserId_Positive()
    {
        Movie movie = new Movie();
        movie.setId(1L);

        WatchMovieUser watchMovieUser = new WatchMovieUser();
        watchMovieUser.movie = movie;
        watchMovieUser.setRating(5);

        User u1 = new User();
        u1.id = 1L;
        u1.watchedMovieUsers = new ArrayList<>();
        u1.watchedMovieUsers.add(watchMovieUser);

        when(userRepository.findById(1L)).thenReturn(u1);

        WatchMovieUser result = userData.updateUserRatingByUserId(1L, 1L, 3);

        assertNotNull(result);
        assertEquals(3, watchMovieUser.getRating());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void updateUserRatingByUserId_UserDoesNotExist()
    {
        User u1 = new User();
        u1.id = 100L;

        when(userRepository.findById(100L)).thenReturn(null);

        assertThrows(WebApplicationException.class, () -> {
            WatchMovieUser result = userData.updateUserRatingByUserId(100L, 2L, 3);
        });
    }

    @Test
    void updateUserRatingByUserId_WatchMovieDoesNotExist()
    {
        User u1 = new User();
        u1.id = 1L;
        u1.watchedMovieUsers = new ArrayList<>();

        when(userRepository.findById(1L)).thenReturn(u1);

        assertThrows(WebApplicationException.class, () -> {
            WatchMovieUser result = userData.updateUserRatingByUserId(1L, 200L, 3);
        });
    }

    @Test
    void updateUserRatingByUserId_MovieDoesNotExist()
    {
        User u1 = new User();
        u1.id = 1L;
        u1.watchedMovieUsers = new ArrayList<>();

        when(userRepository.findById(1L)).thenReturn(u1);
        when(movieRepository.findById(200L)).thenReturn(null);

        assertThrows(WebApplicationException.class, () -> {
            WatchMovieUser result = userData.updateUserRatingByUserId(1L, 200L, 3);
        });
        verify(movieRepository, times(1)).findById(200L);
    }

    @Test
    void updateUserRatingByUserId_CreatedWatchMoviePositive()
    {
        User u1 = new User();
        u1.id = 1L;
        u1.watchedMovieUsers = new ArrayList<>();

        when(userRepository.findById(1L)).thenReturn(u1);
        when(movieRepository.findById(200L)).thenReturn(new Movie());

        WatchMovieUser result = userData.updateUserRatingByUserId(1L, 200L, 3);
        verify(movieRepository, times(1)).findById(200L);
        assertEquals(result.user.id, 1L);
    }
}