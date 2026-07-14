package ca.usherbrooke.fgen.api.business;

import ca.usherbrooke.fgen.api.Business.MovieBusiness;
import ca.usherbrooke.fgen.api.Business.UserBusiness;
import ca.usherbrooke.fgen.api.Business.WatchMovieUserBusiness;
import ca.usherbrooke.fgen.api.Data.WatchMovieUserData;
import ca.usherbrooke.fgen.api.Entities.Movie;
import ca.usherbrooke.fgen.api.Entities.MovieStatus;
import ca.usherbrooke.fgen.api.Entities.User;
import ca.usherbrooke.fgen.api.Entities.WatchMovieUser;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WatchMovieUserBusinessTest {

    private WatchMovieUserData watchMovieUserData;
    private UserBusiness userBusiness;
    private MovieBusiness movieBusiness;
    private WatchMovieUserBusiness watchMovieUserBusiness;

    @BeforeEach
    void setUp() {
        watchMovieUserData = Mockito.mock(WatchMovieUserData.class);
        userBusiness = Mockito.mock(UserBusiness.class);
        movieBusiness = Mockito.mock(MovieBusiness.class);
        watchMovieUserBusiness = new WatchMovieUserBusiness(watchMovieUserData, userBusiness, movieBusiness);
    }

    // Utility helper to simulate that a user exists in the database
    private void mockUserExists(long userId) {
        when(userBusiness.getUserByUserId(userId)).thenReturn(new User());
    }

    @Test
    void validateUserExists_lanceException422_siIdInvalide() {
        WebApplicationException exception = assertThrows(
                WebApplicationException.class,
                () -> watchMovieUserBusiness.getUserSavedListByUserId(-5L)
        );
        assertEquals(422, exception.getResponse().getStatus());
        verifyNoInteractions(watchMovieUserData);
    }

    @Test
    void validateUserExists_lanceException404_siUtilisateurInexistantDansBDD() {
        long nonExistentUserId = 99L;
        when(userBusiness.getUserByUserId(nonExistentUserId)).thenReturn(null);

        WebApplicationException exception = assertThrows(
                WebApplicationException.class,
                () -> watchMovieUserBusiness.getUserSavedListByUserId(nonExistentUserId)
        );
        assertEquals(404, exception.getResponse().getStatus());
        verifyNoInteractions(watchMovieUserData);
    }

    @Test
    void getUserWatchlistByUserId_delegueAWatchMovieUserDataEtRetourneListe() {
        long userId = 1L;
        mockUserExists(userId);

        WatchMovieUser match = new WatchMovieUser();
        when(watchMovieUserData.getSavedListByUserId(userId)).thenReturn(List.of(match));

        List<WatchMovieUser> result = watchMovieUserBusiness.getUserSavedListByUserId(userId);

        assertEquals(1, result.size());
        verify(watchMovieUserData, times(1)).getSavedListByUserId(userId);
    }

    @Test
    void getUserWatchedMoviesByUserId_delegueAWatchMovieUserDataAvecStatusWatched() {
        long userId = 1L;
        mockUserExists(userId);

        when(watchMovieUserData.getByUserIdAndStatus(userId, MovieStatus.WATCHED)).thenReturn(List.of());

        List<WatchMovieUser> result = watchMovieUserBusiness.getUserWatchedMoviesByUserId(userId);

        assertTrue(result.isEmpty());
        verify(watchMovieUserData, times(1)).getByUserIdAndStatus(userId, MovieStatus.WATCHED);
    }

    @Test
    void getUserStartedMoviesByUserId_delegueAWatchMovieUserDataAvecStatusWatching() {
        long userId = 1L;
        mockUserExists(userId);

        when(watchMovieUserData.getByUserIdAndStatus(userId, MovieStatus.WATCHING)).thenReturn(List.of());

        List<WatchMovieUser> result = watchMovieUserBusiness.getUserStartedMoviesByUserId(userId);

        assertTrue(result.isEmpty());
        verify(watchMovieUserData, times(1)).getByUserIdAndStatus(userId, MovieStatus.WATCHING);
    }

    @Test
    void getUserRatingByUserIdAndMovieId_lanceException400_siMovieIdInvalide() {
        long userId = 1L;
        mockUserExists(userId);

        WebApplicationException exception = assertThrows(
                WebApplicationException.class,
                () -> watchMovieUserBusiness.getUserRatingByUserIdAndMovieId(userId, 0L)
        );
        assertEquals(400, exception.getResponse().getStatus());
    }

    @Test
    void getUserRatingByUserIdAndMovieId_lanceException404_siAucunHistorique() {
        long userId = 1L;
        long movieId = 5L;
        mockUserExists(userId);

        when(watchMovieUserData.getByUserIdAndMovieId(userId, movieId)).thenReturn(Optional.empty());

        WebApplicationException exception = assertThrows(
                WebApplicationException.class,
                () -> watchMovieUserBusiness.getUserRatingByUserIdAndMovieId(userId, movieId)
        );
        assertEquals(404, exception.getResponse().getStatus());
    }

    @Test
    void getUserRatingByUserIdAndMovieId_lanceException404_siFilmNonNote() {
        long userId = 1L;
        long movieId = 5L;
        mockUserExists(userId);

        WatchMovieUser historyWithoutRating = new WatchMovieUser();
        historyWithoutRating.setRating(null); // No rating assigned yet

        when(watchMovieUserData.getByUserIdAndMovieId(userId, movieId)).thenReturn(Optional.of(historyWithoutRating));

        WebApplicationException exception = assertThrows(
                WebApplicationException.class,
                () -> watchMovieUserBusiness.getUserRatingByUserIdAndMovieId(userId, movieId)
        );
        assertEquals(404, exception.getResponse().getStatus());
    }

    @Test
    void getUserRatingByUserIdAndMovieId_retourneNoteSiToutEstValide() {
        long userId = 1L;
        long movieId = 5L;
        mockUserExists(userId);

        WatchMovieUser historyWithRating = new WatchMovieUser();
        historyWithRating.setRating(4);

        when(watchMovieUserData.getByUserIdAndMovieId(userId, movieId)).thenReturn(Optional.of(historyWithRating));

        Integer result = watchMovieUserBusiness.getUserRatingByUserIdAndMovieId(userId, movieId);

        assertEquals(4, result);
        verify(watchMovieUserData, times(1)).getByUserIdAndMovieId(userId, movieId);
    }

    @Test
    void getUserSavedMoviesByUserId_returnsMoviesFromSavedInteractions() {
        long userId = 1L;
        mockUserExists(userId);

        Movie movie = new Movie();
        WatchMovieUser interaction = new WatchMovieUser();
        interaction.setMovie(movie);

        when(watchMovieUserData.getSavedListByUserId(userId)).thenReturn(List.of(interaction));

        List<Movie> result = watchMovieUserBusiness.getUserSavedMoviesByUserId(userId);

        assertEquals(List.of(movie), result);
        verify(watchMovieUserData, times(1)).getSavedListByUserId(userId);
    }

    @Test
    void isMovieSavedByUserIdAndMovieId_throws400_whenMovieIdIsInvalid() {
        long userId = 1L;
        mockUserExists(userId);

        WebApplicationException exception = assertThrows(
                WebApplicationException.class,
                () -> watchMovieUserBusiness.isMovieSavedByUserIdAndMovieId(userId, 0L)
        );

        assertEquals(400, exception.getResponse().getStatus());
        verifyNoInteractions(movieBusiness);
    }

    @Test
    void isMovieSavedByUserIdAndMovieId_delegatesToWatchMovieUserData() {
        long userId = 1L;
        long movieId = 5L;
        mockUserExists(userId);

        Movie movie = new Movie();
        when(movieBusiness.getMovieByMovieId(movieId)).thenReturn(movie);
        when(watchMovieUserData.isMovieSavedByUserIdAndMovieId(userId, movieId)).thenReturn(true);

        boolean result = watchMovieUserBusiness.isMovieSavedByUserIdAndMovieId(userId, movieId);

        assertTrue(result);
        verify(watchMovieUserData, times(1)).isMovieSavedByUserIdAndMovieId(userId, movieId);
    }

    @Test
    void updateSavedStatus_validatesUserAndMovieThenDelegatesToData() {
        long userId = 1L;
        long movieId = 5L;

        User user = new User();
        user.setId(userId);
        Movie movie = new Movie();
        movie.setId(movieId);
        WatchMovieUser interaction = new WatchMovieUser();

        when(userBusiness.getUserByUserId(userId)).thenReturn(user);
        when(movieBusiness.getMovieByMovieId(movieId)).thenReturn(movie);
        when(watchMovieUserData.updateSavedStatus(user, movie, true)).thenReturn(interaction);

        WatchMovieUser result = watchMovieUserBusiness.updateSavedStatus(userId, movieId, true);

        assertEquals(interaction, result);
        verify(watchMovieUserData, times(1)).updateSavedStatus(user, movie, true);
    }
}
