package ca.usherbrooke.fgen.api.business;

import ca.usherbrooke.fgen.api.Business.MovieBusiness;
import ca.usherbrooke.fgen.api.Data.MovieData;
import ca.usherbrooke.fgen.api.Entities.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MovieBusinessTest {

    private MovieData movieData;
    private MovieBusiness movieBusiness;

    @BeforeEach
    void setUp() {
        movieData = Mockito.mock(MovieData.class);
        movieBusiness = new MovieBusiness(movieData);
    }

    @Test
    void getAllMovies_delegueAMovieDataEtRetourneListe() {
        // Arrange
        Movie m1 = new Movie();
        m1.title = "Interstellar";
        Movie m2 = new Movie();
        m2.title = "The Matrix";

        when(movieData.getAllMovies()).thenReturn(List.of(m1, m2));

        // Act
        List<Movie> result = movieBusiness.getAllMovies();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Interstellar", result.get(0).title);
        verify(movieData, times(1)).getAllMovies();
    }

    @Test
    void getAllMovies_retourneListeVideSiAucunFilm() {
        // Arrange
        when(movieData.getAllMovies()).thenReturn(List.of());

        // Act
        List<Movie> result = movieBusiness.getAllMovies();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(movieData, times(1)).getAllMovies();
    }

    @Test
    void getMovieByMovieId_delegueAMovieDataEtRetourneMovie() {
        // Arrange
        Movie movie = new Movie();
        movie.title = "Interstellar";
        when(movieData.getMovieByMovieId(1L)).thenReturn(movie);

        // Act
        Movie result = movieBusiness.getMovieByMovieId(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Interstellar", result.title);
        verify(movieData, times(1)).getMovieByMovieId(1L);
    }

    @Test
    void getMovieByMovieId_retourneNull_siMovieInexistant() {
        // Arrange
        when(movieData.getMovieByMovieId(99L)).thenReturn(null);

        // Act
        Movie result = movieBusiness.getMovieByMovieId(99L);

        // Assert
        assertNull(result);
        verify(movieData, times(1)).getMovieByMovieId(99L);
    }
}