package ca.usherbrooke.fgen.api.Data;


import ca.usherbrooke.fgen.api.DAO.MovieRepository;
import ca.usherbrooke.fgen.api.Entities.Movie;
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
}
