package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Business.MovieBusiness;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;



@Path("/public/movie")
public class MoviePresentation {

    private final MovieBusiness movieBusiness;

    @Inject
    public MoviePresentation(MovieBusiness movieBusiness) {
        this.movieBusiness = movieBusiness;
    }


    @GET()
    @Path("ping")
    public String ping() {

        return movieBusiness.ping();
    }

}
