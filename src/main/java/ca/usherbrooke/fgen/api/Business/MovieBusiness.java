package ca.usherbrooke.fgen.api.Business;

import ca.usherbrooke.fgen.api.Data.MovieData;
import ca.usherbrooke.fgen.api.Entities.Movie;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

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

    public List<Movie> getAllMovies(){
        return movieData.getAllMovies();
    }

    public Movie getMovieByMovieId(long id){
        if(id < 0){
            throw new WebApplicationException(
                    Response.status(422)
                            .entity("Unprocessable ID: ID cannot be negative")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }
        Movie movie = movieData.getMovieByMovieId(id);
        if(movie == null){
            throw new WebApplicationException(
                    Response.status(404)
                            .entity("Movie not found")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }
        return movieData.getMovieByMovieId(id);
    }

    public boolean deleteMovieByMovieId(long id) {
        if (id < 0)
            throw new WebApplicationException(
                    Response.status(422)
                            .entity("Unprocessable ID: ID cannot be negative")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        boolean isDeleted = movieData.deleteMovieByMovieId(id);
        if (!isDeleted) {
            throw new WebApplicationException(
                    Response.status(404)
                            .entity("Movie not found")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }
        return movieData.deleteMovieByMovieId(id);
    }
}
