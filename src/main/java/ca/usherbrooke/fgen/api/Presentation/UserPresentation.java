package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Business.UserBusiness;
import ca.usherbrooke.fgen.api.Business.UserService;
import ca.usherbrooke.fgen.api.Entities.Tag;
import ca.usherbrooke.fgen.api.Entities.User;
import ca.usherbrooke.fgen.api.Utils.ExceptionUtils;
import ca.usherbrooke.fgen.api.Utils.SecurityUtils;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/api/user")
@RolesAllowed({"admin"})
public class UserPresentation {
    private final UserBusiness userBusiness;
    private final UserService userService;

    @Inject
    JsonWebToken jwt;

    @Inject
    SecurityContext securityContext;

    @Inject
    public UserPresentation(UserBusiness userBusiness, UserService userService) {
        this.userBusiness = userBusiness;
        this.userService = userService;
    }


    @GET()
    @Path("ping")
    @PermitAll
    public String ping() {
        return userBusiness.ping();
    }

    @GET
    @Path("all")
    @RolesAllowed({"admin"})
    public Response getAllUsers() {
        List<User> users = userBusiness.getAllUsers();
        return Response.ok(users).build();
    }

    @GET
    @Path("{id}")
    @RolesAllowed({"user", "admin"})
    public Response getUserByUserId(@PathParam("id") long id) {
        User user = SecurityUtils.verifyOwnershipOrAdmin(id, userBusiness, jwt, securityContext);
        return Response.ok(user).build();
    }

    @GET
    @Path("me")
    @RolesAllowed({"user", "admin"})
    public Response getCurrentUser() {
        User user = userService.resolveCurrentUser();
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(user).build();
    }

    @PUT
    @Path("{movieId}/{userId}/{newRating}")
    @RolesAllowed({"user", "admin"})
    public Response updateUserRatingByUserId(@PathParam("userId") long id,
                                             @PathParam("movieId") long movieId,
                                             @PathParam("newRating") int newRating)
    {
        SecurityUtils.verifyOwnershipOrAdmin(id, userBusiness, jwt, securityContext);
        ca.usherbrooke.fgen.api.Entities.WatchMovieUser updated =
                userBusiness.updateUserRatingByUserId(id, movieId, newRating);
        return Response.ok(updated).build();
    }

    @POST
    @Path("register")
    @PermitAll
    public Response registerUser(User registrationPayload) {

        System.out.println(registrationPayload.toString());

        User created = userBusiness.registerNewUser(
                registrationPayload.getUsername(),
                registrationPayload.getEmail(),
                registrationPayload.getKeycloakId()
        );
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("{id}/enable")
    @RolesAllowed({"admin"})
    public Response enableUser(@PathParam("id") long id) {
        userBusiness.enableUser(id);

        return Response.ok()
                .entity("{\"message\":\"User enabled\"}")
                .build();
    }


    @PUT
    @Path("{id}/disable")
    @RolesAllowed({"admin"})
    public Response disableUser(@PathParam("id") long id) {
        userBusiness.disableUser(id);

        return Response.ok()
                .entity("{\"message\":\"User disabled\"}")
                .build();
    }
}