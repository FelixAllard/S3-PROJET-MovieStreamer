package ca.usherbrooke.fgen.api.business;

import ca.usherbrooke.fgen.api.Business.UserBusiness;
import ca.usherbrooke.fgen.api.Data.UserData;
import ca.usherbrooke.fgen.api.Entities.Movie;
import ca.usherbrooke.fgen.api.Entities.Tag;
import ca.usherbrooke.fgen.api.Entities.User;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserBusinessTest {

    private UserData userData;
    private UserBusiness userBusiness;

    @BeforeEach
    void setUp() {
        userData = Mockito.mock(UserData.class);
        userBusiness = new UserBusiness(userData);
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
    void updateUserRatingByUserId_PositiveTest()
    {
        User user = new User();
        user.id = 1L;
        when(userData.updateUserRatingByUserId(1L, 2L, 3)).thenReturn(user);

        User result = userBusiness.updateUserRatingByUserId(1L, 2L, 3);

        assertNotNull(result);
        assertEquals(user.id, result.id);
        verify(userData, times(1)).updateUserRatingByUserId(1L, 2L, 3);
    }

    @Test
    void updateUserRatingByUserId_NegativeTest()
    {
        assertThrows(WebApplicationException.class, () -> {
            User result = userBusiness.updateUserRatingByUserId(1L, 2L, 11);
        });
    }
}