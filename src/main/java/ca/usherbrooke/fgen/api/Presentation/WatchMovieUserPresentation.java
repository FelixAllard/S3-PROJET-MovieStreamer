package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Business.WatchMovieUserBusiness;
import ca.usherbrooke.fgen.api.Business.UserBusiness;
import ca.usherbrooke.fgen.api.Entities.WatchMovieUser;
import ca.usherbrooke.fgen.api.Utils.SecurityUtils;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/api/watch-history")
@RolesAllowed({"admin"})
@Produces(MediaType.APPLICATION_JSON)
public class WatchMovieUserPresentation {

    private final WatchMovieUserBusiness watchMovieUserBusiness;
    private final UserBusiness userBusiness;

    @Inject
    JsonWebToken jwt;

    @Inject
    SecurityContext securityContext;

    @Inject
    public WatchMovieUserPresentation(WatchMovieUserBusiness watchMovieUserBusiness, UserBusiness userBusiness) {
        this.watchMovieUserBusiness = watchMovieUserBusiness;
        this.userBusiness = userBusiness;
    }

    @GET
    @Path("watchlist/{userId}")
    @RolesAllowed({"user", "admin"})
    public Response getUserWatchlistByUserId(@PathParam("userId") long userId) {
        SecurityUtils.verifyOwnershipOrAdmin(userId, userBusiness, jwt, securityContext);
        List<WatchMovieUser> watchlist = watchMovieUserBusiness.getUserWatchlistByUserId(userId);
        return Response.ok(watchlist).build();
    }

    @GET
    @Path("watched/{userId}")
    @RolesAllowed({"user", "admin"})
    public Response getUserWatchedMoviesByUserId(@PathParam("userId") long userId) {
        SecurityUtils.verifyOwnershipOrAdmin(userId, userBusiness, jwt, securityContext);
        List<WatchMovieUser> watchedMovies = watchMovieUserBusiness.getUserWatchedMoviesByUserId(userId);
        return Response.ok(watchedMovies).build();
    }

    @GET
    @Path("started/{userId}")
    @RolesAllowed({"user", "admin"})
    public Response getUserStartedMoviesByUserId(@PathParam("userId") long userId) {
        SecurityUtils.verifyOwnershipOrAdmin(userId, userBusiness, jwt, securityContext);
        List<WatchMovieUser> startedMovies = watchMovieUserBusiness.getUserStartedMoviesByUserId(userId);
        return Response.ok(startedMovies).build();
    }

    @GET
    @Path("rating/{userId}/{movieId}")
    @RolesAllowed({"user", "admin"})
    public Response getUserRatingByUserIdAndMovieId(
            @PathParam("userId") long userId,
            @PathParam("movieId") long movieId) {

        SecurityUtils.verifyOwnershipOrAdmin(userId, userBusiness, jwt, securityContext);
        Integer rating = watchMovieUserBusiness.getUserRatingByUserIdAndMovieId(userId, movieId);
        return Response.ok(rating).build();
    }
}