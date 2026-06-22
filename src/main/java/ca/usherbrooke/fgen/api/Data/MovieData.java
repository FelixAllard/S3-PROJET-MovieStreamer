package ca.usherbrooke.fgen.api.Data;


import ca.usherbrooke.fgen.api.DAO.MovieRepository;
import ca.usherbrooke.fgen.api.Entities.Movie;
import ca.usherbrooke.fgen.api.Entities.WatchMovieUser;
import ca.usherbrooke.fgen.api.Utils.ExceptionUtils;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class MovieData {
    private final MovieRepository movieRepository;

    public MovieData(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public String ping(){
        return "pong!";
    }

    public List<Movie> getAllMovies(){
        return movieRepository.listAll();
    }

    public Movie getMovieByMovieId(long id){
        return movieRepository.findById(id);
    }

    public Movie getMovieByMovieName(String name) {
        return movieRepository.find("title", name).firstResult();
    }

    public List<Object[]> getMovieRatingByMovieId(long id) {
        Movie movie = movieRepository.findById(id);

        if(movie == null)
            ExceptionUtils.throwException(404, "Movie Not Found");

        return movieRepository.getRatingDistributionByMovieId(id);
    }
    public List<Movie> getNewMovies(int number){

        return movieRepository.findNewestMovies(number);
    }
}
