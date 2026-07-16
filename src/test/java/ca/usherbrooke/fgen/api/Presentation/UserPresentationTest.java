package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Business.UserBusiness;
import ca.usherbrooke.fgen.api.Business.UserService;
import ca.usherbrooke.fgen.api.Entities.Tag;
import ca.usherbrooke.fgen.api.Entities.User;
import ca.usherbrooke.fgen.api.Entities.WatchMovieUser;
import ca.usherbrooke.fgen.api.Utils.SecurityUtils;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ExceptionUtils;
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



        userPresentation = new UserPresentation(
                userBusiness,
                userService);

        userPresentation.jwt = jwt;
        userPresentation.securityContext = securityContext;

    }
    private void mockAdminAccess(long userId) {
        User user = new User();
        user.setId(userId);
        user.setKeycloakId("admin-uuid-bypass");

        when(securityContext.isUserInRole("admin")).thenReturn(true);
        when(userBusiness.getUserByUserId(userId)).thenReturn(user);
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
        mockAdminAccess(user.id);

        WatchMovieUser watchMovieUser = new WatchMovieUser();
        watchMovieUser.id =1L;

        when(userBusiness.updateUserRatingByUserId(1L, 2L, 3)).thenReturn(watchMovieUser);

        Response response = userPresentation.updateUserRatingByUserId(1L, 2L, 3);

        assertEquals(200, response.getStatus());
        verify(userBusiness, times(1)).updateUserRatingByUserId(1L, 2L, 3);
    }
    @Test
    void disableUser_retourneStatus200SiSucces() {
        doNothing().when(userBusiness).disableUser(1L);

        Response response = userPresentation.disableUser(1L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(userBusiness, times(1)).disableUser(1L);
    }

    @Test
    void disableUser_retourneStatus400SiIdInvalide() {
        doThrow(new WebApplicationException(
                Response.status(400).entity("Invalid user ID provided.").build()
        )).when(userBusiness).disableUser(0L);

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> userPresentation.disableUser(0L));

        assertEquals(400, ex.getResponse().getStatus());
        verify(userBusiness, times(1)).disableUser(0L);
    }

    @Test
    void disableUser_retourneStatus404SiUserInexistant() {
        doThrow(new WebApplicationException(
                Response.status(404).entity("User not found in database.").build()
        )).when(userBusiness).disableUser(99L);

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> userPresentation.disableUser(99L));

        assertEquals(404, ex.getResponse().getStatus());
        verify(userBusiness, times(1)).disableUser(99L);
    }

    @Test
    void disableUser_retourneStatus500SiKeycloakEchoue() {
        doThrow(new WebApplicationException(
                Response.status(500).entity("Failed to disable user in Keycloak.").build()
        )).when(userBusiness).disableUser(1L);

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> userPresentation.disableUser(1L));

        assertEquals(500, ex.getResponse().getStatus());
        verify(userBusiness, times(1)).disableUser(1L);
    }

    @Test
    void enableUser_retourneStatus200SiSucces() {
        doNothing().when(userBusiness).enableUser(1L);

        Response response = userPresentation.enableUser(1L);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(userBusiness, times(1)).enableUser(1L);
    }

    @Test
    void enableUser_retourneStatus400SiIdInvalide() {
        doThrow(new WebApplicationException(
                Response.status(400).entity("Invalid user ID provided.").build()
        )).when(userBusiness).enableUser(0L);

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> userPresentation.enableUser(0L));

        assertEquals(400, ex.getResponse().getStatus());
        verify(userBusiness, times(1)).enableUser(0L);
    }

    @Test
    void enableUser_retourneStatus404SiUserInexistant() {
        doThrow(new WebApplicationException(
                Response.status(404).entity("User not found in database.").build()
        )).when(userBusiness).enableUser(99L);

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> userPresentation.enableUser(99L));

        assertEquals(404, ex.getResponse().getStatus());
        verify(userBusiness, times(1)).enableUser(99L);
    }

    @Test
    void enableUser_retourneStatus500SiKeycloakEchoue() {
        doThrow(new WebApplicationException(
                Response.status(500).entity("Failed to enable user in Keycloak.").build()
        )).when(userBusiness).enableUser(1L);

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> userPresentation.enableUser(1L));

        assertEquals(500, ex.getResponse().getStatus());
        verify(userBusiness, times(1)).enableUser(1L);
    }

    @Test
    void getCurrentUser_me_succes_retourneStatus200AvecUtilisateurConnecte() {
        // Arrange
        User currentUser = new User();
        currentUser.setId(10L);
        currentUser.setUsername("activeUser");
        currentUser.setEmail("active@example.com");

        when(userService.resolveCurrentUser()).thenReturn(currentUser);

        // Act
        Response response = userPresentation.getCurrentUser();

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(currentUser, response.getEntity());
        verify(userService, times(1)).resolveCurrentUser();
    }

    @Test
    void getCurrentUser_me_retourneStatus404_siContextUtilisateurIntrouvable() {
        // Arrange
        when(userService.resolveCurrentUser()).thenReturn(null);

        // Act
        Response response = userPresentation.getCurrentUser();

        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertNull(response.getEntity());
        verify(userService, times(1)).resolveCurrentUser();
    }
}