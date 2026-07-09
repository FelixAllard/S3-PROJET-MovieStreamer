package ca.usherbrooke.fgen.api.business;

import ca.usherbrooke.fgen.api.Business.MovieBusiness;
import ca.usherbrooke.fgen.api.Data.MovieData;
import ca.usherbrooke.fgen.api.Entities.Movie;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

    static Movie createValidMovie() {
        Movie m = new Movie();
        m.title = "Inception";
        m.description = "A dream within a dream";
        m.year = 2010;
        m.director = "Christopher Nolan";
        m.studio = "Warner Bros";
        m.writer = "Nolan";
        m.movieLength = 148;
        m.language = "English";
        m.thumbnail = "img.jpg";
        return m;
    }
    static Movie movieWith(java.util.function.Consumer<Movie> modifier) {
        Movie m = createValidMovie();
        modifier.accept(m);
        return m;
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
        when(movieData.getMovieByMovieId(99L)).thenReturn(null);

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> movieBusiness.getMovieByMovieId(99L));

        assertEquals(404, ex.getResponse().getStatus());
        verify(movieData, times(1)).getMovieByMovieId(99L);
    }

    @Test
    void deleteMovieByMovieId_delegueAMovieDataEtRetourneTrue() {
        when(movieData.deleteMovieByMovieId(1L)).thenReturn(true);

        boolean result = movieBusiness.deleteMovieByMovieId(1L);

        assertTrue(result);
        verify(movieData, times(1)).deleteMovieByMovieId(1L);
    }

    @Test
    void deleteMovieByMovieId_retourneFalseSiMovieInexistant() {
        when(movieData.deleteMovieByMovieId(99L)).thenReturn(false);

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> movieBusiness.deleteMovieByMovieId(99L));

        assertEquals(404, ex.getResponse().getStatus());
        verify(movieData, times(1)).deleteMovieByMovieId(99L);
    }

    void getMovieByMovieName_delegueAMovieDataEtRetourneMovie() {
        Movie movie = new Movie();
        movie.title = "Interstellar";
        when(movieData.getMovieByMovieName("Interstellar")).thenReturn(movie);

        Movie result = movieBusiness.getMovieByMovieName("Interstellar");

        assertNotNull(result);
        assertEquals("Interstellar", result.title);
        verify(movieData, times(1)).getMovieByMovieName("Interstellar");
    }

    @Test
    void getMovieByMovieName_nameNullOuVideLanceException() {
        assertThrows(WebApplicationException.class, () -> movieBusiness.getMovieByMovieName(null));
        assertThrows(WebApplicationException.class, () -> movieBusiness.getMovieByMovieName(""));
    }

    @Test
    void getMovieByMovieName_movieInexistantLanceException() {
        when(movieData.getMovieByMovieName("Inexistant")).thenReturn(null);

        assertThrows(WebApplicationException.class, () -> movieBusiness.getMovieByMovieName("Inexistant"));
        verify(movieData, times(1)).getMovieByMovieName("Inexistant");
    }

    @Test
    void getMovieRatingByMovieId_FormatteLesRatingsEtCalculeLeBonAverage(){
        //Arrange
        List<Object[]> ratings = List.of(new Object[] {1,2L}, new Object[] {2,1L}, new Object[] {3,1L}, new Object[] {4,8L});

        when(movieData.getMovieRatingByMovieId(1L)).thenReturn(ratings);

        //Act
        Map<String, Object> result = movieBusiness.getMovieRatingByMovieId(1L);

        //Assert
        assertNotNull(result);
        assertEquals(3.25, result.get("average"));

        // Vérifier le formattage
        List<Map<String, Object>> distribution = (List<Map<String, Object>>) result.get("distribution");
        assertEquals(4, distribution.size());
        assertEquals(1, distribution.get(0).get("rating"));
        assertEquals(2L, distribution.get(0).get("count"));
    }

    @Test
    void getMovieRatingByMovieId_ThrowLesBonnesExceptions(){
        //Arrange
        when(movieData.getMovieRatingByMovieId(1L)).thenReturn(List.of());

        //Assert
        WebApplicationException exceptionListeVide =
                assertThrows(WebApplicationException.class, () -> movieBusiness.getMovieRatingByMovieId(1L));

        assertEquals(404, exceptionListeVide.getResponse().getStatus());

        WebApplicationException exceptionIdNegatif =
                assertThrows(WebApplicationException.class, () -> movieBusiness.getMovieRatingByMovieId(-1L));

        assertEquals(422, exceptionIdNegatif.getResponse().getStatus());
    }


    @ParameterizedTest
    @MethodSource("invalidMovies")
    void shouldRejectInvalidMovies(Movie movie, String expectedMessage) {

        RuntimeException ex = assertThrows(
                WebApplicationException.class,
                () -> movieBusiness.postMovie(movie)
        );
    }

    // ----------------------------
    // TEST DATA ITERATION
    // ----------------------------
    static Stream<Arguments> invalidMovies() {

        Movie base = createValidMovie();

        return Stream.of(

                // NULL MOVIE
                org.junit.jupiter.params.provider.Arguments.of(
                        null,
                        "Movie Null"
                ),

                // EMPTY TITLE
                org.junit.jupiter.params.provider.Arguments.of(
                        movieWith(m -> m.title = ""),
                        "Movie Title Empty"
                ),

                // NULL TITLE
                org.junit.jupiter.params.provider.Arguments.of(
                        movieWith(m -> m.title = null),
                        "Movie Title Empty"
                ),

                // EMPTY DESCRIPTION
                org.junit.jupiter.params.provider.Arguments.of(
                        movieWith(m -> m.description = ""),
                        "Movie Description Empty"
                ),

                // INVALID YEAR
                org.junit.jupiter.params.provider.Arguments.of(
                        movieWith(m -> m.year = -1),
                        "Movie Year Invalid"
                ),

                // EMPTY DIRECTOR
                org.junit.jupiter.params.provider.Arguments.of(
                        movieWith(m -> m.director = ""),
                        "Movie Director Empty"
                ),

                // EMPTY STUDIO
                org.junit.jupiter.params.provider.Arguments.of(
                        movieWith(m -> m.studio = ""),
                        "Movie Studio Empty"
                ),

                // EMPTY WRITER
                org.junit.jupiter.params.provider.Arguments.of(
                        movieWith(m -> m.writer = ""),
                        "Movie WriterEmpty"
                ),

                // INVALID LENGTH
                org.junit.jupiter.params.provider.Arguments.of(
                        movieWith(m -> m.movieLength = -10),
                        "Movie Length Invalid"
                ),

                // EMPTY LANGUAGE
                org.junit.jupiter.params.provider.Arguments.of(
                        movieWith(m -> m.language = ""),
                        "Movie Language Empty"
                ),

                // EMPTY THUMBNAIL
                org.junit.jupiter.params.provider.Arguments.of(
                        movieWith(m -> m.thumbnail = ""),
                        "Movie Thumbnail Empty"
                )
        );
    }
    @Test
    void getMoviesByMovieTags_delegueAMovieDataEtRetourneListe() {
        List<Integer> tagIds = List.of(1, 2);
        List<Movie> movies = List.of(new Movie(), new Movie());
        when(movieData.getMoviesByMovieTags(tagIds)).thenReturn(movies);

        List<Movie> result = movieBusiness.getMoviesByMovieTags(tagIds);

        assertEquals(2, result.size());
        verify(movieData, times(1)).getMoviesByMovieTags(tagIds);
    }

    @Test
    void getMoviesByMovieTags_tagIdsNullOuVideLanceException() {
        assertThrows(WebApplicationException.class, () -> movieBusiness.getMoviesByMovieTags(null));
        assertThrows(WebApplicationException.class, () -> movieBusiness.getMoviesByMovieTags(List.of()));
    }

    @Test
    void getMoviesByMovieTags_retourne204SiAucunFilmTrouve() {
        List<Integer> tagIds = List.of(1);
        when(movieData.getMoviesByMovieTags(tagIds)).thenReturn(List.of());

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> movieBusiness.getMoviesByMovieTags(tagIds));

        assertEquals(204, ex.getResponse().getStatus());
    }

    @Test
    void updateMovieByMovieId_delegueAMovieDataEtRetourneMovie() {
        Movie input = createValidMovie();
        when(movieData.updateMovieByMovieId(1, input)).thenReturn(input);

        Movie result = movieBusiness.updateMovieByMovieId(1, input);

        assertEquals(input, result);
        verify(movieData, times(1)).updateMovieByMovieId(1, input);
    }

    @Test
    void updateMovieByMovieId_valideLesChampsEtLanceException() {
        assertThrows(WebApplicationException.class, () -> movieBusiness.updateMovieByMovieId(-1, new Movie()));

        assertThrows(WebApplicationException.class, () -> movieBusiness.updateMovieByMovieId(1, null));

        Movie invalidMovie = createValidMovie();
        invalidMovie.title = "";

        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> movieBusiness.updateMovieByMovieId(1, invalidMovie));
        assertEquals(422, ex.getResponse().getStatus());
    }

    @Test
    void searchMovies_delegueAMovieDataEtRetourneListe() {
        // Arrange
        List<Integer> tags = List.of(1, 2);
        List<Movie> movies = List.of(new Movie(), new Movie());

        when(movieData.searchMovies(tags, 2000, 2020, "English", "Nolan", "WB", "Nolan", "Inter"))
                .thenReturn(movies);

        // Act
        List<Movie> result = movieBusiness.searchMovies(tags, 2000, 2020, "English", "Nolan", "WB", "Nolan", "Inter");

        // Assert
        assertEquals(2, result.size());
        verify(movieData, times(1)).searchMovies(tags, 2000, 2020, "English", "Nolan", "WB", "Nolan", "Inter");
    }

    @Test
    void searchMovies_lanceExceptionSiYearMinSuperieurAYearMax() {
        // Act & Assert
        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> movieBusiness.searchMovies(null, 2020, 2000, null, null, null, null, null)
        );
        assertEquals(422, ex.getResponse().getStatus());
        verify(movieData, never()).searchMovies(any(), any(), any(), any(), any(), any(), any(), any());
    }


}
