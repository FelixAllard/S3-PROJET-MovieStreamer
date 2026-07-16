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
    void getAllUsers_delegueAUserServiceEtRetourneListe() {
        User u1 = new User();
        User u2 = new User();
        when(userService.getAllUsersWithStatus()).thenReturn(List.of(u1, u2));

        List<User> result = userBusiness.getAllUsers();

        assertEquals(2, result.size());
        verify(userService, times(1)).getAllUsersWithStatus();
    }

    @Test
    void getAllUsers_retourneListeVideSiAucunUtilisateur() {
        when(userService.getAllUsersWithStatus()).thenReturn(List.of());

        List<User> result = userBusiness.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userService, times(1)).getAllUsersWithStatus();
    }
    @Test
    void getUserByUserId_delegueAUserServiceEtRetourneUtilisateur() {
        User user = new User();

        when(userService.getUserByIdWithStatus(1L))
                .thenReturn(user);

        User result = userBusiness.getUserByUserId(1L);

        assertEquals(user, result);

        verify(userService, times(1))
                .getUserByIdWithStatus(1L);
    }

    @Test
    void getUserByUserId_retourne404_siUtilisateurInexistant() {
        when(userService.getUserByIdWithStatus(99L))
                .thenReturn(null);

        WebApplicationException ex = assertThrows(
                WebApplicationException.class,
                () -> userBusiness.getUserByUserId(99L)
        );

        assertEquals(404, ex.getResponse().getStatus());

        verify(userService, times(1))
                .getUserByIdWithStatus(99L);
    }

    @Test
    void disableUser_appelleServiceAvecIdValide() {
        doNothing().when(userService).disableOrDisableUser(1L, false);

        userBusiness.disableUser(1L);

        verify(userService, times(1)).disableOrDisableUser(1L, false);
    }

    @Test
    void disableUser_lanceExceptionSiIdInvalide() {
        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> userBusiness.disableUser(0L));

        assertEquals(400, ex.getResponse().getStatus());
        verify(userService, never()).disableOrDisableUser(anyLong(), eq(false));
    }

    @Test
    void disableUser_lanceExceptionSiIdNegatif() {
        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> userBusiness.disableUser(-5L));

        assertEquals(400, ex.getResponse().getStatus());
        verify(userService, never()).disableOrDisableUser(anyLong(), eq(false));
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