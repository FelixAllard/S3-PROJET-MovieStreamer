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

    @Test
    void getMovieByMovieId_retourneStatus200AvecMovie() {
        // Arrange
        Movie movie = new Movie();
        when(movieBusiness.getMovieByMovieId(1L)).thenReturn(movie);

        // Act
        Response response = moviePresentation.getMovieByMovieId(1L);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(movie, response.getEntity());
        verify(movieBusiness, times(1)).getMovieByMovieId(1L);
    }

    @Test
    void getMovieByMovieId_retourneStatus404_siMovieInexistant() {
        // Arrange
        when(movieBusiness.getMovieByMovieId(99L)).thenReturn(null);

        // Act
        Response response = moviePresentation.getMovieByMovieId(99L);

        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(movieBusiness, times(1)).getMovieByMovieId(99L);
    }

    @Test
    void deleteMovieByMovieId_retourneStatus204SiMovieSupprime() {
        when(movieBusiness.deleteMovieByMovieId(1L)).thenReturn(true);

        Response response = moviePresentation.deleteMovieByMovieId(1L);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(movieBusiness, times(1)).deleteMovieByMovieId(1L);
    }

    @Test
    void deleteMovieByMovieId_retourneStatus404SiMovieInexistant() {
        when(movieBusiness.deleteMovieByMovieId(99L)).thenReturn(false);

        Response response = moviePresentation.deleteMovieByMovieId(99L);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(movieBusiness, times(1)).deleteMovieByMovieId(99L);
    }
}