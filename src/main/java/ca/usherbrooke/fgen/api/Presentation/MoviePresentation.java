package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Business.MovieBusiness;
import ca.usherbrooke.fgen.api.Business.UserService;
import ca.usherbrooke.fgen.api.Entities.Movie;
import ca.usherbrooke.fgen.api.Entities.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

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
    @PermitAll
    public Response getAllMovies() {
        List<Movie> users = movieBusiness.getAllMovies();
        return Response.ok(users).build();
    }

    @GET
    @Path("{id}")
    @PermitAll
    public Response getMovieByMovieId(@PathParam("id") long id)
    {
        Movie movies = movieBusiness.getMovieByMovieId(id);

        if(movies == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(movies).build();
    }

    @GET
    @Path("name/{name}")
    @PermitAll
    public Response getMovieByMovieName(@PathParam("name") String name) {
        Movie movie = movieBusiness.getMovieByMovieName(name);
        return Response.ok(movie).build();
    }

    @GET
    @Path("{id}/rating")
    @PermitAll
    public Response getMovieRatingByMovieId(@PathParam("id") long id){
        Map<String, Object> ratings = movieBusiness.getMovieRatingByMovieId(id);
        return Response.ok(ratings).build();
    }

    @GET
    @Path("new/{number}")
    @PermitAll
    public Response getNewMovies(@PathParam("number") int number){
        List<Movie> movies = movieBusiness.getNewMovies(number);
        return Response.ok(movies).build();
    }

    @POST
    @Path("")
    @RolesAllowed({"admin"})
    public Response postMovie(Movie movie) {
        Movie created = movieBusiness.postMovie(movie);
        return Response.status(Response.Status.CREATED).entity(created).build();

    }
}
