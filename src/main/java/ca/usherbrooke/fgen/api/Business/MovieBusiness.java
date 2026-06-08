package ca.usherbrooke.fgen.api.Business;

import ca.usherbrooke.fgen.api.Data.MovieData;
import ca.usherbrooke.fgen.api.Entities.Movie;
import ca.usherbrooke.fgen.api.Utils.ExceptionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

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
}
