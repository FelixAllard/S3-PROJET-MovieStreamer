package ca.usherbrooke.fgen.api.Data;

import ca.usherbrooke.fgen.api.DAO.MovieRepository; // Adjust name/package if your DAO is named differently
import ca.usherbrooke.fgen.api.DAO.TagRepository;
import ca.usherbrooke.fgen.api.Entities.Movie;
import ca.usherbrooke.fgen.api.Entities.Tag;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MovieDataTest {

    private MovieRepository movieRepository;
    private MovieData movieData;

    @BeforeEach
    void setUp() {
        movieRepository = Mockito.mock(MovieRepository.class);

        movieData = new MovieData(movieRepository);

    }

    @Test
    void getAllMovies_delegueAuRepositoryEtRetourneListe() {
        // Arrange
        Movie m1 = new Movie();
        Movie m2 = new Movie();
        when(movieRepository.listAll()).thenReturn(List.of(m1, m2));

        // Act
        List<Movie> result = movieData.getAllMovies();

        // Assert
        assertEquals(2, result.size());
        verify(movieRepository, times(1)).listAll();
    }

    @Test
    void getAllMovies_retourneListeVideSiRepositoryVide() {
        // Arrange
        when(movieRepository.listAll()).thenReturn(List.of());

        // Act
        List<Movie> result = movieData.getAllMovies();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(movieRepository, times(1)).listAll();
    }

    @Test
    void getMovieByMovieId_delegueAuRepositoryEtRetourneMovie() {
        // Arrange
        Movie movie = new Movie();
        when(movieRepository.findById(1L)).thenReturn(movie);

        // Act
        Movie result = movieData.getMovieByMovieId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(movie, result);
        verify(movieRepository, times(1)).findById(1L);
    }

    @Test
    void getMovieByMovieId_retourneNull_siIdInexistant() {
        // Arrange
        when(movieRepository.findById(99L)).thenReturn(null);

        // Act
        Movie result = movieData.getMovieByMovieId(99L);

        // Assert
        assertNull(result);
        verify(movieRepository, times(1)).findById(99L);
    }

    @Test
    void getMovieByMovieName_delegueAuRepositoryEtRetourneMovie() {
        Movie movie = new Movie();
        movie.title = "Interstellar";
        @SuppressWarnings("unchecked")
        PanacheQuery<Movie> query = Mockito.mock(PanacheQuery.class);
        when(movieRepository.find("title", "Interstellar")).thenReturn(query);
        when(query.firstResult()).thenReturn(movie);

        Movie result = movieData.getMovieByMovieName("Interstellar");

        assertNotNull(result);
        assertEquals(movie, result);
        verify(movieRepository, times(1)).find("title", "Interstellar");
        verify(query, times(1)).firstResult();
    }

    @Test
    void getMovieByMovieName_retourneNull_siNomInexistant() {
        @SuppressWarnings("unchecked")
        PanacheQuery<Movie> query = Mockito.mock(PanacheQuery.class);
        when(movieRepository.find("title", "Inexistant")).thenReturn(query);
        when(query.firstResult()).thenReturn(null);

        Movie result = movieData.getMovieByMovieName("Inexistant");

        assertNull(result);
        verify(movieRepository, times(1)).find("title", "Inexistant");
        verify(query, times(1)).firstResult();
    }

    @Test
    void getMovieRatingByMovieId_delegueAuRepoEtRetourneRatings(){
        // Arrange
        Movie movie = new Movie();
        List<Object[]> ratings = List.of();

        when(movieRepository.findById(1L)).thenReturn(movie);
        when(movieRepository.getRatingDistributionByMovieId(1L)).thenReturn(ratings);

        // Act
        List<Object[]> result = movieData.getMovieRatingByMovieId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(ratings, result);
        verify(movieRepository, times(1)).getRatingDistributionByMovieId(1L);
    }

    @Test
    void getMovieRatingByMovieId_retourne404SiFilmInexistant() {
        // Arrange
        when(movieRepository.findById(1L)).thenReturn(null);

        // Act & Assert
        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            movieData.getMovieRatingByMovieId(1L);
        });

        assertEquals(404, exception.getResponse().getStatus());
    }

    @Test
    void shouldCreateMovieSuccessfully() {

        Movie movie = new Movie();
        movie.title = "Inception";
        movie.description = "Dream movie";
        movie.year = 2010;
        movie.director = "Nolan";
        movie.studio = "WB";
        movie.writer = "Nolan";
        movie.movieLength = 120;
        movie.language = "English";
        movie.thumbnail = "img.jpg";

        // No duplicate title
        when(movieRepository.count("title", movie.title)).thenReturn(0L);

        Movie result = movieData.postMovie(movie);

        assertNotNull(result);

        verify(movieRepository).persist(movie);

        assertNull(movie.getTags());
        assertNull(movie.getWatchedMovieUsers());
    }
    @Test
    void shouldThrowExceptionWhenTitleAlreadyExists() {

        Movie movie = new Movie();
        movie.title = "Inception";

        when(movieRepository.count("title", movie.title)).thenReturn(1L);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> movieData.postMovie(movie)
        );

    }
}
