package ca.usherbrooke.fgen.api.Business;

import ca.usherbrooke.fgen.api.Data.MovieData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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

}
