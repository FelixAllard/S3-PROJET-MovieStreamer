package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Business.UserBusiness;
import ca.usherbrooke.fgen.api.Business.UserService;
import ca.usherbrooke.fgen.api.Entities.Tag;
import ca.usherbrooke.fgen.api.Entities.User;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserPresentationTest {

    private UserBusiness userBusiness;
    private UserService userService;
    private JsonWebToken jwt;
    private SecurityContext securityContext;
    private UserPresentation userPresentation;

    @BeforeEach
    void setUp() {
        userBusiness = Mockito.mock(UserBusiness.class);
        userService = Mockito.mock(UserService.class);
        jwt = Mockito.mock(JsonWebToken.class);
        securityContext = Mockito.mock(SecurityContext.class);

        userPresentation = new UserPresentation(userBusiness, userService);

        userPresentation.jwt = jwt;
        userPresentation.securityContext = securityContext;
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
        long targetId = 1L;
        User user = new User();
        user.setId(targetId);
        user.setKeycloakId("mocked-uuid-1111");

        when(securityContext.isUserInRole("admin")).thenReturn(true);
        when(userBusiness.getUserByUserId(targetId)).thenReturn(user);

        Response response = userPresentation.getUserByUserId(targetId);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(user, response.getEntity());

        verify(userBusiness, atLeastOnce()).getUserByUserId(targetId);
    }

    @Test
    void getUserByUserId_retourneStatus404_siUtilisateurInexistant() {
        long unknownId = 99L;
        when(securityContext.isUserInRole("admin")).thenReturn(true);
        when(userBusiness.getUserByUserId(unknownId)).thenReturn(null);

        jakarta.ws.rs.WebApplicationException exception = assertThrows(
                jakarta.ws.rs.WebApplicationException.class,
                () -> userPresentation.getUserByUserId(unknownId)
        );

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
    }

    @Test
    void updateUserRatingByUserId_PositiveTest()
    {
        User user = new User();
        user.id = 1L;
        when(userBusiness.updateUserRatingByUserId(1L, 2L, 3)).thenReturn(user);

        Response response = userPresentation.updateUserRatingByUserId(1L, 2L, 3);

        assertEquals(200, response.getStatus());
        verify(userBusiness, times(1)).updateUserRatingByUserId(1L, 2L, 3);
    }
}