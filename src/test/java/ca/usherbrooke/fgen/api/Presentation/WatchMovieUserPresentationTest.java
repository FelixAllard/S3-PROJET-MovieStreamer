package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Business.WatchMovieUserBusiness;
import ca.usherbrooke.fgen.api.Business.UserBusiness;
import ca.usherbrooke.fgen.api.Entities.User;
import ca.usherbrooke.fgen.api.Entities.WatchMovieUser;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WatchMovieUserPresentationTest {

    private WatchMovieUserBusiness watchMovieUserBusiness;
    private UserBusiness userBusiness;
    private JsonWebToken jwt;
    private SecurityContext securityContext;
    private WatchMovieUserPresentation presentation;

    @BeforeEach
    void setUp() {
        watchMovieUserBusiness = Mockito.mock(WatchMovieUserBusiness.class);
        userBusiness = Mockito.mock(UserBusiness.class);
        jwt = Mockito.mock(JsonWebToken.class);
        securityContext = Mockito.mock(SecurityContext.class);

        // Instantiate the controller
        presentation = new WatchMovieUserPresentation(watchMovieUserBusiness, userBusiness);

        // Manually bind the injected container fields
        presentation.jwt = jwt;
        presentation.securityContext = securityContext;
    }

    private void mockAdminAccess(long userId) {
        User user = new User();
        user.setId(userId);
        user.setKeycloakId("admin-uuid-bypass");

        when(securityContext.isUserInRole("admin")).thenReturn(true);
        when(userBusiness.getUserByUserId(userId)).thenReturn(user);
    }


    @Test
    void getUserSavedListByUserId_retourneStatus200AvecListe() {
        long userId = 1L;
        mockAdminAccess(userId);

        WatchMovieUser item = new WatchMovieUser();
        when(watchMovieUserBusiness.getUserSavedListByUserId(userId)).thenReturn(List.of(item));

        Response response = presentation.getUserSavedListByUserId(userId);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(List.of(item), response.getEntity());
        verify(watchMovieUserBusiness, times(1)).getUserSavedListByUserId(userId);
    }


    @Test
    void getUserWatchedMoviesByUserId_retourneStatus200AvecListe() {
        long userId = 1L;
        mockAdminAccess(userId);

        WatchMovieUser item = new WatchMovieUser();
        when(watchMovieUserBusiness.getUserWatchedMoviesByUserId(userId)).thenReturn(List.of(item));

        Response response = presentation.getUserWatchedMoviesByUserId(userId);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(List.of(item), response.getEntity());
        verify(watchMovieUserBusiness, times(1)).getUserWatchedMoviesByUserId(userId);
    }


    @Test
    void getUserStartedMoviesByUserId_retourneStatus200AvecListe() {
        long userId = 1L;
        mockAdminAccess(userId);

        WatchMovieUser item = new WatchMovieUser();
        when(watchMovieUserBusiness.getUserStartedMoviesByUserId(userId)).thenReturn(List.of(item));

        Response response = presentation.getUserStartedMoviesByUserId(userId);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(List.of(item), response.getEntity());
        verify(watchMovieUserBusiness, times(1)).getUserStartedMoviesByUserId(userId);
    }


    @Test
    void getUserRatingByUserIdAndMovieId_retourneStatus200AvecNote() {
        long userId = 1L;
        long movieId = 42L;
        mockAdminAccess(userId);

        when(watchMovieUserBusiness.getUserRatingByUserIdAndMovieId(userId, movieId)).thenReturn(5);

        Response response = presentation.getUserRatingByUserIdAndMovieId(userId, movieId);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(5, response.getEntity());
        verify(watchMovieUserBusiness, times(1)).getUserRatingByUserIdAndMovieId(userId, movieId);
    }


    @Test
    void endpoint_retourneStatus403_siUtilisateurEssaieDAccederAutreUtilisateur() {
        long pathUserId = 1L;

        User targetUser = new User();
        targetUser.setId(pathUserId);
        targetUser.setKeycloakId("user-1111-uuid");

        // Simulate a standard user who is NOT an admin
        when(securityContext.isUserInRole("admin")).thenReturn(false);
        when(userBusiness.getUserByUserId(pathUserId)).thenReturn(targetUser);

        // Simulate that the token belongs to a completely different user (User 2)
        when(jwt.getSubject()).thenReturn("user-2222-different-uuid");

        // Running any history API method should throw a 403 Forbidden via ExceptionUtils
        WebApplicationException exception = assertThrows(
                WebApplicationException.class,
                () -> presentation.getUserSavedListByUserId(pathUserId)
        );

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), exception.getResponse().getStatus());

        // Ensure the business layer logic is never contacted when security validation fails
        verifyNoInteractions(watchMovieUserBusiness);
    }
}