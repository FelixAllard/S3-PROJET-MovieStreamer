package ca.usherbrooke.fgen.api.Data;

import ca.usherbrooke.fgen.api.DAO.MovieRepository; // Adjust name/package if your DAO is named differently
import ca.usherbrooke.fgen.api.Entities.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
}