package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Business.MovieBusiness;
import ca.usherbrooke.fgen.api.Business.UserService;
import ca.usherbrooke.fgen.api.Entities.Movie;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MoviePresentationTest {

    private MovieBusiness movieBusiness;
    private MoviePresentation moviePresentation;
    private UserService userService;

    @BeforeEach
    void setUp() {
        movieBusiness = Mockito.mock(MovieBusiness.class);
        moviePresentation = new MoviePresentation(movieBusiness, userService);
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
    void getMovieByMovieName_retourneStatus200AvecMovie() {
        Movie movie = new Movie();
        movie.title = "Interstellar";
        when(movieBusiness.getMovieByMovieName("Interstellar")).thenReturn(movie);

        Response response = moviePresentation.getMovieByMovieName("Interstellar");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(movie, response.getEntity());
        verify(movieBusiness, times(1)).getMovieByMovieName("Interstellar");
    }

    @Test
    void getMovieRatingByMovieId_retourne200AvecRatings(){
        // Arrange
        Map<String, Object> mockRatings = new HashMap<>();
        mockRatings.put("average", 3.8);
        mockRatings.put("distribution", List.of());

        when(movieBusiness.getMovieRatingByMovieId(1L)).thenReturn(mockRatings);

        // Act
        Response response = moviePresentation.getMovieRatingByMovieId(1L);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(mockRatings, response.getEntity());
        verify(movieBusiness, times(1)).getMovieRatingByMovieId(1L);
    }

    @Test
    void getNewMovies_Retourn200(){
        Movie movie = new Movie();
        movie.title = "Interstellar";
        List<Movie> movies = new ArrayList<>();
        movies.add(movie);

        when(movieBusiness.getNewMovies(3)).thenReturn(movies);

        // Act
        Response response = moviePresentation.getNewMovies(3);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(movies, response.getEntity());
    }
    @Test
    void postMovie_Retourn200(){
        Movie movie = new Movie();
        movie.title = "Interstellar";
        when(movieBusiness.postMovie(movie)).thenReturn(movie);

        // Act
        Response response = moviePresentation.postMovie(movie);

        // Assert
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(movie, response.getEntity());
    }




}
