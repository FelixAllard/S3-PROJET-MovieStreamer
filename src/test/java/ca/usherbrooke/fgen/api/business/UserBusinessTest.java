package ca.usherbrooke.fgen.api.business;

import ca.usherbrooke.fgen.api.Business.UserBusiness;
import ca.usherbrooke.fgen.api.Data.UserData;
import ca.usherbrooke.fgen.api.Entities.User;
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
}