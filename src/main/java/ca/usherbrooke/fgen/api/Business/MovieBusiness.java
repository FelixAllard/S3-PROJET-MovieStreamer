package ca.usherbrooke.fgen.api.Business;

import ca.usherbrooke.fgen.api.Data.MovieData;
import ca.usherbrooke.fgen.api.Entities.Movie;
import ca.usherbrooke.fgen.api.Utils.ExceptionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class MovieBusiness {
    private final MovieData movieData;


    @Inject
    public MovieBusiness(MovieData movieData) {
        this.movieData = movieData;
    }


    public String ping() {
        return movieData.ping();
    }

    public List<Movie> getAllMovies(){
        return movieData.getAllMovies();
    }

    public Movie getMovieByMovieId(long id){
        if(id < 0){
            throw new WebApplicationException(
                    Response.status(422)
                            .entity("Unprocessable ID: ID cannot be negative")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }
        Movie movie = movieData.getMovieByMovieId(id);
        if(movie == null){
            throw new WebApplicationException(
                    Response.status(404)
                            .entity("Movie not found")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }
        return movieData.getMovieByMovieId(id);
    }

    public boolean deleteMovieByMovieId(long id) {
        if (id < 0)
            throw new WebApplicationException(
                    Response.status(422)
                            .entity("Unprocessable ID: ID cannot be negative")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        boolean isDeleted = movieData.deleteMovieByMovieId(id);
        if (!isDeleted) {
            throw new WebApplicationException(
                    Response.status(404)
                            .entity("Movie not found")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }
        return isDeleted;
    }

    public Movie getMovieByMovieName(String name) {
        if (name == null)
            ExceptionUtils.throwException(400, "Movie Name Null");
        if (name.isEmpty())
            ExceptionUtils.throwException(422, "Movie Name Cannot Be Empty");

        Movie movie = movieData.getMovieByMovieName(name);
        if (movie == null)
            ExceptionUtils.throwException(404, "Movie Not Found");

        return movie;
    }

    public Map<String, Object> getMovieRatingByMovieId(long id) {
        if(id <= 0)
            ExceptionUtils.throwException(422, "Unprocessable ID: ID cannot be negative");

        List<Object[]> rawRatings = movieData.getMovieRatingByMovieId(id);

        if(rawRatings.isEmpty())
            ExceptionUtils.throwException(404, "No ratings found for this movie");

        List<Map<String, Object>> distribution = new ArrayList<>();
        double totalRatings = 0;
        double totalCount = 0;

        for (Object[] r : rawRatings) {
            int rating = (int) r[0];
            long count = (long) r[1];

            Map<String, Object> row = new HashMap<>();
            row.put("rating", rating);
            row.put("count", count);
            distribution.add(row);

            totalRatings += rating * count;
            totalCount += count;
        }

        Map<String, Object> formattedRatings = new HashMap<>();
        formattedRatings.put("average", totalRatings / totalCount);
        formattedRatings.put("distribution", distribution);
        return formattedRatings;
    }
    public List<Movie> getNewMovies(int number){
        if(number <= 0)
            ExceptionUtils.throwException(422, "Unprocessable ID: ID cannot be negative");
        List<Movie> movies = movieData.getNewMovies(number);
        if(movies.isEmpty())
            ExceptionUtils.throwException(204, "No content");
        return movies;
    }
    public Movie postMovie(Movie movie) {
        if(movie == null)
            ExceptionUtils.throwException(422, "Movie Null");
        if(movie.description.isEmpty())
            ExceptionUtils.throwException(422, "Movie Description Empty");
        if(movie.title == null || movie.title.isEmpty())
            ExceptionUtils.throwException(422, "Movie Title Empty");
        if(movie.year < 0)
            ExceptionUtils.throwException(422, "Movie Year Invalid");
        if(movie.director == null || movie.director.isEmpty())
            ExceptionUtils.throwException(422, "Movie Director Empty");
        if(movie.studio ==null || movie.studio.isEmpty())
            ExceptionUtils.throwException(422, "Movie Studio Empty");
        if(movie.writer == null || movie.writer.isEmpty())
            ExceptionUtils.throwException(422, "Movie WriterEmpty");
        if(movie.movieLength < 0)
            ExceptionUtils.throwException(422, "Movie Length Invalid");
        if(movie.language == null || movie.language.isEmpty())
            ExceptionUtils.throwException(422, "Movie Language Empty");
        if(movie.thumbnail == null || movie.thumbnail.isEmpty())
            ExceptionUtils.throwException(422, "Movie Thumbnail Empty");
        return movieData.postMovie(movie);
    }
}
