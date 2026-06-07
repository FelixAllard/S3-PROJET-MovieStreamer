package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Business.UserBusiness;
import ca.usherbrooke.fgen.api.Business.UserService;
import ca.usherbrooke.fgen.api.Entities.User;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserPresentationTest {

    private UserBusiness userBusiness;
    private UserPresentation userPresentation;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userBusiness = Mockito.mock(UserBusiness.class);
        userPresentation = new UserPresentation(userBusiness, userService);
    }

    @Test
    void getAllUsers_retourneStatus200AvecListeUtilisateurs() {
        User u1 = new User();
        User u2 = new User();
        when(userBusiness.getAllUsers()).thenReturn(List.of(u1, u2));

        Response response = userPresentation.getAllUsers();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(List.of(u1, u2), response.getEntity());
        verify(userBusiness, times(1)).getAllUsers();
    }

    @Test
    void getAllUsers_retourneStatus200AvecListeVide() {
        when(userBusiness.getAllUsers()).thenReturn(List.of());

        Response response = userPresentation.getAllUsers();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(((List<?>) response.getEntity()).isEmpty());
    }
    @Test
    void getUserByUserId_retourneStatus200AvecUtilisateur() {
        User user = new User();
        when(userBusiness.getUserByUserId(1L)).thenReturn(user);

        Response response = userPresentation.getUserByUserId(1L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(user, response.getEntity());
        verify(userBusiness, times(1)).getUserByUserId(1L);
    }

    @Test
    void getUserByUserId_retourneStatus404_siUtilisateurInexistant() {
        when(userBusiness.getUserByUserId(99L)).thenReturn(null);

        Response response = userPresentation.getUserByUserId(99L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(userBusiness, times(1)).getUserByUserId(99L);
    }

    @Test
    void deleteUserByUserId_retourneStatus204SiUserSupprime() {
        when(userBusiness.deleteUserByUserId(1L)).thenReturn(true);

        Response response = userPresentation.deleteUserByUserId(1L);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(userBusiness, times(1)).deleteUserByUserId(1L);
    }

    @Test
    void deleteUserByUserId_retourneStatus404SiUserInexistant() {
        when(userBusiness.deleteUserByUserId(99L)).thenReturn(false);

        Response response = userPresentation.deleteUserByUserId(99L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(userBusiness, times(1)).deleteUserByUserId(99L);
    }

}