package ca.usherbrooke.fgen.api.Data;

import ca.usherbrooke.fgen.api.DAO.WatchMovieUserRepository;
import ca.usherbrooke.fgen.api.Entities.MovieStatus;
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
    void getWatchlistByUserId_delegueAuRepositoryEtRetourneListe() {
        long userId = 1L;
        WatchMovieUser item1 = new WatchMovieUser();
        WatchMovieUser item2 = new WatchMovieUser();

        when(watchMovieUserRepository.list("user.id = ?1 and saved = true", userId))
                .thenReturn(List.of(item1, item2));

        List<WatchMovieUser> result = watchMovieUserData.getWatchlistByUserId(userId);

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
}