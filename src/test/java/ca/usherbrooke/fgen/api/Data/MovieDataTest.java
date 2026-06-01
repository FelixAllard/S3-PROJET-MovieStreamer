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
}