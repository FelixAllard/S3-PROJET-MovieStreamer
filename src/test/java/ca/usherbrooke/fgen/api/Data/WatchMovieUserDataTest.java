package ca.usherbrooke.fgen.api.Data;

import ca.usherbrooke.fgen.api.DAO.WatchMovieUserRepository;
import ca.usherbrooke.fgen.api.Entities.Movie;
import ca.usherbrooke.fgen.api.Entities.MovieStatus;
import ca.usherbrooke.fgen.api.Entities.User;
import ca.usherbrooke.fgen.api.Entities.WatchMovieUser;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WatchMovieUserDataTest {

    private WatchMovieUserRepository watchMovieUserRepository;
    private WatchMovieUserData watchMovieUserData;

    @BeforeEach
    void setUp() {
        watchMovieUserRepository = Mockito.mock(WatchMovieUserRepository.class);
        watchMovieUserData = new WatchMovieUserData(watchMovieUserRepository);
    }


    @Test
    void getSavedListByUserId_delegueAuRepositoryEtRetourneListe() {
        long userId = 1L;
        WatchMovieUser item1 = new WatchMovieUser();
        WatchMovieUser item2 = new WatchMovieUser();

        when(watchMovieUserRepository.list("user.id = ?1 and saved = true", userId))
                .thenReturn(List.of(item1, item2));

        List<WatchMovieUser> result = watchMovieUserData.getSavedListByUserId(userId);

        assertEquals(2, result.size());
        verify(watchMovieUserRepository, times(1))
                .list("user.id = ?1 and saved = true", userId);
    }


    @Test
    void getByUserIdAndStatus_delegueAuRepositoryAvecStatusWatched() {
        long userId = 1L;
        MovieStatus status = MovieStatus.WATCHED;
        WatchMovieUser item = new WatchMovieUser();

        when(watchMovieUserRepository.list("user.id = ?1 and status = ?2", userId, status))
                .thenReturn(List.of(item));

        List<WatchMovieUser> result = watchMovieUserData.getByUserIdAndStatus(userId, status);

        assertEquals(1, result.size());
        verify(watchMovieUserRepository, times(1))
                .list("user.id = ?1 and status = ?2", userId, status);
    }

    @Test
    void getByUserIdAndStatus_delegueAuRepositoryAvecStatusWatching() {
        long userId = 1L;
        MovieStatus status = MovieStatus.WATCHING;

        when(watchMovieUserRepository.list("user.id = ?1 and status = ?2", userId, status))
                .thenReturn(List.of());

        List<WatchMovieUser> result = watchMovieUserData.getByUserIdAndStatus(userId, status);

        assertTrue(result.isEmpty());
        verify(watchMovieUserRepository, times(1))
                .list("user.id = ?1 and status = ?2", userId, status);
    }


    @Test
    void getByUserIdAndMovieId_retourneOptionalAvecInteraction_siTrouve() {
        long userId = 1L;
        long movieId = 10L;
        WatchMovieUser interaction = new WatchMovieUser();

        // Mock the intermediate PanacheQuery fluid interface chain
        PanacheQuery<WatchMovieUser> mockQuery = Mockito.mock(PanacheQuery.class);
        when(watchMovieUserRepository.find("user.id = ?1 and movie.id = ?2", userId, movieId))
                .thenReturn(mockQuery);
        when(mockQuery.firstResultOptional())
                .thenReturn(Optional.of(interaction));

        Optional<WatchMovieUser> result = watchMovieUserData.getByUserIdAndMovieId(userId, movieId);

        assertTrue(result.isPresent());
        assertEquals(interaction, result.get());
        verify(watchMovieUserRepository, times(1))
                .find("user.id = ?1 and movie.id = ?2", userId, movieId);
    }

    @Test
    void getByUserIdAndMovieId_retourneOptionalVide_siNonTrouve() {
        long userId = 1L;
        long movieId = 99L;

        PanacheQuery<WatchMovieUser> mockQuery = Mockito.mock(PanacheQuery.class);
        when(watchMovieUserRepository.find("user.id = ?1 and movie.id = ?2", userId, movieId))
                .thenReturn(mockQuery);
        when(mockQuery.firstResultOptional())
                .thenReturn(Optional.empty());

        Optional<WatchMovieUser> result = watchMovieUserData.getByUserIdAndMovieId(userId, movieId);

        assertTrue(result.isEmpty());
        verify(watchMovieUserRepository, times(1))
                .find("user.id = ?1 and movie.id = ?2", userId, movieId);
    }

    @Test
    void isMovieSavedByUserIdAndMovieId_returnsTrueWhenRepositoryCountsOneRow() {
        long userId = 1L;
        long movieId = 10L;

        when(watchMovieUserRepository.count("user.id = ?1 and movie.id = ?2 and saved = true", userId, movieId))
                .thenReturn(1L);

        boolean result = watchMovieUserData.isMovieSavedByUserIdAndMovieId(userId, movieId);

        assertTrue(result);
        verify(watchMovieUserRepository, times(1))
                .count("user.id = ?1 and movie.id = ?2 and saved = true", userId, movieId);
    }

    @Test
    void updateSavedStatus_updatesExistingInteraction() {
        long userId = 1L;
        long movieId = 10L;
        User user = new User();
        user.setId(userId);
        Movie movie = new Movie();
        movie.setId(movieId);
        WatchMovieUser interaction = new WatchMovieUser();
        interaction.setSaved(false);

        PanacheQuery<WatchMovieUser> mockQuery = Mockito.mock(PanacheQuery.class);
        when(watchMovieUserRepository.find("user.id = ?1 and movie.id = ?2", userId, movieId))
                .thenReturn(mockQuery);
        when(mockQuery.firstResultOptional())
                .thenReturn(Optional.of(interaction));

        WatchMovieUser result = watchMovieUserData.updateSavedStatus(user, movie, true);

        assertEquals(interaction, result);
        assertTrue(result.isSaved());
        verify(watchMovieUserRepository, never()).persist(any(WatchMovieUser.class));
    }

    @Test
    void updateSavedStatus_createsInteractionWhenMissing() {
        long userId = 1L;
        long movieId = 10L;
        User user = new User();
        user.setId(userId);
        Movie movie = new Movie();
        movie.setId(movieId);

        PanacheQuery<WatchMovieUser> mockQuery = Mockito.mock(PanacheQuery.class);
        when(watchMovieUserRepository.find("user.id = ?1 and movie.id = ?2", userId, movieId))
                .thenReturn(mockQuery);
        when(mockQuery.firstResultOptional())
                .thenReturn(Optional.empty());

        WatchMovieUser result = watchMovieUserData.updateSavedStatus(user, movie, true);

        assertEquals(user, result.getUser());
        assertEquals(movie, result.getMovie());
        assertEquals(MovieStatus.NOT_WATCHED, result.getStatus());
        assertTrue(result.isSaved());
        verify(watchMovieUserRepository, times(1)).persist(result);
    }
}
