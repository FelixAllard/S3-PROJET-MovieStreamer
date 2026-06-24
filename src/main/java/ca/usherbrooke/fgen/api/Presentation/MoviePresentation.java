package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Business.MovieBusiness;
import ca.usherbrooke.fgen.api.Entities.Movie;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.util.List;

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

    @GET
    @Path("all")
    public Response getAllMovies() {
        List<Movie> users = movieBusiness.getAllMovies();
        return Response.ok(users).build();
    }

    @GET
    @Path("{id}")
    public Response getMovieByMovieId(@PathParam("id") long id)
    {
        Movie movies = movieBusiness.getMovieByMovieId(id);
        return Response.ok(movies).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteMovieByMovieId(@PathParam("id") long id) {
        movieBusiness.deleteMovieByMovieId(id);
        return Response.noContent().build();
    }
}
