package ca.usherbrooke.fgen.api.Buisness;

import ca.usherbrooke.fgen.api.Data.MovieData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MovieBuisness {
    private final MovieData movieData;


    @Inject
    public MovieBuisness(MovieData movieData) {
        this.movieData = movieData;
    }


    public String ping() {
        return movieData.ping();
    }

}
