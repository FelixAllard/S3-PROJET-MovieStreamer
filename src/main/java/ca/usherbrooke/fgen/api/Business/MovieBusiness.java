package ca.usherbrooke.fgen.api.Business;

import ca.usherbrooke.fgen.api.Data.MovieData;
import ca.usherbrooke.fgen.api.Entities.Movie;
import ca.usherbrooke.fgen.api.Utils.ExceptionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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
        return movieData.getMovieByMovieId(id);
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
}
