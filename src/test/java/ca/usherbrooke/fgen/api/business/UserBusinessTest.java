package ca.usherbrooke.fgen.api.business;

import ca.usherbrooke.fgen.api.Business.UserBusiness;
import ca.usherbrooke.fgen.api.Business.UserService;
import ca.usherbrooke.fgen.api.Data.UserData;
import ca.usherbrooke.fgen.api.Entities.User;
import ca.usherbrooke.fgen.api.Entities.WatchMovieUser;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserBusinessTest {

    private UserData userData;
    private UserService userService;
    private UserBusiness userBusiness;

    @BeforeEach
    void setUp() {
        userData = Mockito.mock(UserData.class);
        userService = Mockito.mock(UserService.class);
        userBusiness = new UserBusiness(userData, userService);
    }

    @Test
    void getAllUsers_delegueAUserDataEtRetourneListe() {
        User u1 = new User();
        User u2 = new User();
        when(userData.getAllUsers()).thenReturn(List.of(u1, u2));

        List<User> result = userBusiness.getAllUsers();

        assertEquals(2, result.size());
        verify(userData, times(1)).getAllUsers();
    }

    @Test
    void getAllUsers_retourneListeVideSiAucunUtilisateur() {
        when(userData.getAllUsers()).thenReturn(List.of());

        List<User> result = userBusiness.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userData, times(1)).getAllUsers();
    }
    @Test
    void getUserByUserId_delegueAUserDataEtRetourneUtilisateur() {
        User user = new User();
        when(userData.getUserByUserId(1L)).thenReturn(user);

        User result = userBusiness.getUserByUserId(1L);

        assertEquals(user, result);
        verify(userData, times(1)).getUserByUserId(1L);
    }

    @Test
    void getUserByUserId_retourneNull_siUtilisateurInexistant() {
        when(userData.getUserByUserId(99L)).thenReturn(null);

        User result = userBusiness.getUserByUserId(99L);

        assertNull(result);
        verify(userData, times(1)).getUserByUserId(99L);
    }

    @Test
    void deleteUserByUserId_delegueAUserDataEtRetourneTrue() {
        when(userData.deleteUserByUserId(1L)).thenReturn(true);

        boolean result = userBusiness.deleteUserByUserId(1L);

        assertTrue(result);
        verify(userData, times(1)).deleteUserByUserId(1L);
    }

    @Test
    void deleteUserByUserId_retourneFalseSiUserInexistant() {
        when(userData.deleteUserByUserId(99L)).thenReturn(false);

        boolean result = userBusiness.deleteUserByUserId(99L);

        assertFalse(result);
        verify(userData, times(1)).deleteUserByUserId(99L);
    }

    @Test
    void updateUserRatingByUserId_PositiveTest()
    {
        WatchMovieUser watchMovieUser = new WatchMovieUser();
        watchMovieUser.id =100l;

        when(userData.updateUserRatingByUserId(1L, 2L, 3)).thenReturn(watchMovieUser);

        WatchMovieUser result = userBusiness.updateUserRatingByUserId(1L, 2L, 3);

        assertNotNull(result);
        assertEquals(watchMovieUser.id, result.id);
        verify(userData, times(1)).updateUserRatingByUserId(1L, 2L, 3);
    }

    @Test
    void updateUserRatingByUserId_RatingTooHigh()
    {
        assertThrows(WebApplicationException.class, () -> {
            WatchMovieUser result = userBusiness.updateUserRatingByUserId(1L, 2L, 11);
        });
    }

    @Test
    void updateUserRatingByUserId_RatingTooLow()
    {
        assertThrows(WebApplicationException.class, () -> {
            WatchMovieUser result = userBusiness.updateUserRatingByUserId(1L, 2L, -1);
        });
    }

    @Test
    void updateUserRatingByUserId_UserIdNegative()
    {
        assertThrows(WebApplicationException.class, () -> {
            WatchMovieUser result = userBusiness.updateUserRatingByUserId(-1L, 2L, 3);
        });
    }

    @Test
    void updateUserRatingByUserId_MovieIdNegative()
    {
        assertThrows(WebApplicationException.class, () -> {
            WatchMovieUser result = userBusiness.updateUserRatingByUserId(1L, -2L, 3);
        });
    }

    @Test
    void registerNewUser_succes_delegueAUserService() {
        // Arrange
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";

        User expectedUser = new User();
        expectedUser.setUsername(username);
        expectedUser.setEmail(email);

        // Stub the mock userService dependency directly
        when(userService.registerNewUser(username, email, password)).thenReturn(expectedUser);

        // Act
        User result = userBusiness.registerNewUser(username, email, password);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userService, times(1)).registerNewUser(username, email, password);
    }

    @Test
    void registerNewUser_champsInvalides_lanceExceptionSansAppelerLeService() {
        // Act & Assert (UserBusiness validation validation checks for blank username)
        assertThrows(WebApplicationException.class, () -> {
            userBusiness.registerNewUser("", "test@example.com", "password123");
        });

        // Verify it was caught by your business validation guard rails before touching the service
        verify(userService, never()).registerNewUser(anyString(), anyString(), anyString());
    }
}