package ca.usherbrooke.fgen.api.Data;


import ca.usherbrooke.fgen.api.DAO.MovieRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MovieData {
    private final MovieRepository movieRepository;

    public MovieData(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public String ping(){
        return "pong!";
    }
}
