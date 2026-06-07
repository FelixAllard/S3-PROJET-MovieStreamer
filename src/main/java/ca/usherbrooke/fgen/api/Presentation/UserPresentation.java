package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Business.UserBusiness;
import ca.usherbrooke.fgen.api.Business.UserService;
import ca.usherbrooke.fgen.api.Entities.User;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/user")
@RolesAllowed({"admin"})
public class UserPresentation {
    private final UserBusiness userBusiness;
    private final UserService userService;

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
    @RolesAllowed({"admin"})
    public Response getUserByUserId(@PathParam("id") long id) {
        User users = userBusiness.getUserByUserId(id);
        //return 404
        if(users == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(users).build();
    }
}