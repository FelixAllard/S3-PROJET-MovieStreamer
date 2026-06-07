package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Business.MovieBusiness;
import ca.usherbrooke.fgen.api.Business.UserService;
import ca.usherbrooke.fgen.api.Entities.Movie;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/movie")
@RolesAllowed({"admin"})
public class MoviePresentation {

    private final MovieBusiness movieBusiness;
    private final UserService userService;

    @Inject
    public MoviePresentation(MovieBusiness movieBusiness, UserService userService) {
        this.movieBusiness = movieBusiness;
        this.userService = userService;
    }

    @GET()
    @Path("ping")
    @PermitAll
    public String ping() {
        return movieBusiness.ping();
    }

    @GET
    @Path("all")
    @RolesAllowed({"user", "admin"})
    public Response getAllMovies() {
        List<Movie> users = movieBusiness.getAllMovies();
        return Response.ok(users).build();
    }

    @GET
    @Path("{id}")
    @RolesAllowed({"user", "admin"})
    public Response getMovieByMovieId(@PathParam("id") long id)
    {
        Movie movies = movieBusiness.getMovieByMovieId(id);

        if(movies == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(movies).build();
    }

}
