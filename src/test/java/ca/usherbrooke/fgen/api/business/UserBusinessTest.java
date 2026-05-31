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
}