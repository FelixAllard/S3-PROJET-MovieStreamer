package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Business.MovieBusiness;
import ca.usherbrooke.fgen.api.Business.UserService;
import ca.usherbrooke.fgen.api.Entities.Movie;
import ca.usherbrooke.fgen.api.Entities.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
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
        return Response.ok(movies).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteMovieByMovieId(@PathParam("id") long id) {
        movieBusiness.deleteMovieByMovieId(id);
        return Response.noContent().build();
    }
    
    @GET
    @Path("name/{name}")
    @PermitAll
    public Response getMovieByMovieName(@PathParam("name") String name) {
        Movie movie = movieBusiness.getMovieByMovieName(name);
        return Response.ok(movie).build();
    }

    @GET
    @Path("partial-title/{title}")
    @PermitAll
    public Response getMoviesByPartialTitle(@PathParam("title") String title){
        List<Movie> movies = movieBusiness.getMoviesByPartialTitle(title);
        return Response.ok(movies).build();
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

    @GET
    @Path("tags")
    @PermitAll
    public Response getMoviesByMovieTags(@QueryParam("tagIds") List<Integer> tagIds) {
        List<Movie> movies = movieBusiness.getMoviesByMovieTags(tagIds);
        return Response.ok(movies).build();
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({"admin"})
    public Response updateMovieByMovieId(@PathParam("id") int id, Movie movie) {
        Movie updated = movieBusiness.updateMovieByMovieId(id, movie);
        return Response.ok(updated).build();
    }

    @GET
    @Path("search")
    @PermitAll
    public Response searchMovies(
            @QueryParam("tags") List<Integer> tags,
            @QueryParam("yearMin") Integer yearMin,
            @QueryParam("yearMax") Integer yearMax,
            @QueryParam("language") String language,
            @QueryParam("director") String director,
            @QueryParam("studio") String studio,
            @QueryParam("writer") String writer,
            @QueryParam("title") String title  // ← ajouté
    ) {
        List<Movie> movies = movieBusiness.searchMovies(tags, yearMin, yearMax, language, director, studio, writer, title);
        return Response.ok(movies).build();
    }
}
