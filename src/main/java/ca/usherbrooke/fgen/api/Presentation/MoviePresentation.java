package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Buisness.MovieBuisness;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;



@Path("/public/movie")
public class MoviePresentation {

    private final MovieBuisness movieBuisness;

    @Inject
    public MoviePresentation(MovieBuisness movieBuisness) {
        this.movieBuisness = movieBuisness;
    }


    @GET()
    @Path("ping")
    public String ping() {

        return movieBuisness.ping();
    }

}
