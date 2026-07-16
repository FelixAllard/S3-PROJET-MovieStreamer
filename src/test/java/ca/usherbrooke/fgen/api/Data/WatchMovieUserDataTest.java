package ca.usherbrooke.fgen.api.Data;

import ca.usherbrooke.fgen.api.DAO.MovieRepository;
import ca.usherbrooke.fgen.api.DAO.WatchMovieUserRepository;
import ca.usherbrooke.fgen.api.Entities.*;
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
    private MovieRepository movieRepository;
    private WatchMovieUserData watchMovieUserData;

    @BeforeEach
    void setUp() {
        watchMovieUserRepository = Mockito.mock(WatchMovieUserRepository.class);
        movieRepository = Mockito.mock(MovieRepository.class);
        watchMovieUserData = new WatchMovieUserData(watchMovieUserRepository, movieRepository);
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

    @Test
    void getUserMovieRecommendationByUserId_retourneListeVide_siAucunFilmSauvegarde() {
        long userId = 1L;

        when(watchMovieUserRepository.list("user.id = ?1 and saved = true", userId))
                .thenReturn(List.of());

        List<Movie> result = watchMovieUserData.getUserMovieRecommendationByUserId(userId);

        assertTrue(result.isEmpty());
        verifyNoInteractions(movieRepository);
    }

    @Test
    void getUserMovieRecommendationByUserId_retourneListeVide_siAucunTagPreferre() {
        long userId = 1L;

        Movie savedMovie = new Movie();
        savedMovie.setId(1L);
        savedMovie.setTags(List.of());

        WatchMovieUser interaction = new WatchMovieUser();
        interaction.setMovie(savedMovie);

        when(watchMovieUserRepository.list("user.id = ?1 and saved = true", userId))
                .thenReturn(List.of(interaction));

        List<Movie> result = watchMovieUserData.getUserMovieRecommendationByUserId(userId);

        assertTrue(result.isEmpty());
        verifyNoInteractions(movieRepository);
    }

    @Test
    void getUserMovieRecommendationByUserId_retourneFilmsPartageantUnTagEtExcluLesDejaSauvegardes() {
        long userId = 1L;

        Tag actionTag = new Tag();
        actionTag.setId(100);

        Movie savedMovie = new Movie();
        savedMovie.setId(1L);
        savedMovie.setTags(List.of(actionTag));

        WatchMovieUser interaction = new WatchMovieUser();
        interaction.setMovie(savedMovie);

        Movie matchingMovie = new Movie();
        matchingMovie.setId(2L);
        matchingMovie.setTags(List.of(actionTag));

        Movie nonMatchingMovie = new Movie();
        nonMatchingMovie.setId(3L);
        nonMatchingMovie.setTags(List.of());

        when(watchMovieUserRepository.list("user.id = ?1 and saved = true", userId))
                .thenReturn(List.of(interaction));
        when(movieRepository.listAll())
                .thenReturn(List.of(savedMovie, matchingMovie, nonMatchingMovie));

        List<Movie> result = watchMovieUserData.getUserMovieRecommendationByUserId(userId);

        assertEquals(List.of(matchingMovie), result);
        verify(movieRepository, times(1)).listAll();
    }
}

