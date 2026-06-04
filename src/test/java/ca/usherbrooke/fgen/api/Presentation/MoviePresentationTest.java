package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Business.MovieBusiness;
import ca.usherbrooke.fgen.api.Entities.Movie;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MoviePresentationTest {

    private MovieBusiness movieBusiness;
    private MoviePresentation moviePresentation;

    @BeforeEach
    void setUp() {
        movieBusiness = Mockito.mock(MovieBusiness.class);
        moviePresentation = new MoviePresentation(movieBusiness);
    }

    @Test
    void getAllMovies_retourneStatus200AvecListeFilms() {
        // Arrange
        Movie m1 = new Movie();
        Movie m2 = new Movie();
        when(movieBusiness.getAllMovies()).thenReturn(List.of(m1, m2));

        // Act
        Response response = moviePresentation.getAllMovies();

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(List.of(m1, m2), response.getEntity());
        verify(movieBusiness, times(1)).getAllMovies();
    }

    @Test
    void getAllMovies_retourneStatus200AvecListeVide() {
        // Arrange
        when(movieBusiness.getAllMovies()).thenReturn(List.of());

        // Act
        Response response = moviePresentation.getAllMovies();

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(((List<?>) response.getEntity()).isEmpty());
        verify(movieBusiness, times(1)).getAllMovies();
    }
}