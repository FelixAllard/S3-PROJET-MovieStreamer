package ca.usherbrooke.fgen.api.business;

import ca.usherbrooke.fgen.api.Business.UserBusiness;
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
    void postRatingByUserAndMovieID_delegueAUserDataEtRetourneWMU() {
        // Arrange
        WatchMovieUser mockWmu = new WatchMovieUser();
        mockWmu.setRating(4);

        when(userData.postRatingByUserAndMovieID(5L, 10L, 4)).thenReturn(mockWmu);

        // Act
        WatchMovieUser result = userBusiness.postRatingByUserAndMovieID(5L, 10L, 4);

        // Assert
        assertNotNull(result);
        verify(userData, times(1)).postRatingByUserAndMovieID(5L, 10L, 4);
    }

    @Test
    void postRatingByUserAndMovieID_ThrowLesBonnesExceptions(){
        WebApplicationException userIdNegatif = assertThrows(WebApplicationException.class, () ->
                userBusiness.postRatingByUserAndMovieID(-1L, 10L, 4)
        );
        assertEquals(422, userIdNegatif.getResponse().getStatus());

        WebApplicationException movieIdNegatif = assertThrows(WebApplicationException.class, () ->
                userBusiness.postRatingByUserAndMovieID(5L, -1L, 4)
        );
        assertEquals(422, movieIdNegatif.getResponse().getStatus());

        WebApplicationException ratingTropGrand = assertThrows(WebApplicationException.class, () ->
                userBusiness.postRatingByUserAndMovieID(5L, 10L, 11)
        );
        assertEquals(422, ratingTropGrand.getResponse().getStatus());

        WebApplicationException ratingNegatif = assertThrows(WebApplicationException.class, () ->
                userBusiness.postRatingByUserAndMovieID(5L, 10L, -1)
        );
        assertEquals(422, ratingNegatif.getResponse().getStatus());
    }
}