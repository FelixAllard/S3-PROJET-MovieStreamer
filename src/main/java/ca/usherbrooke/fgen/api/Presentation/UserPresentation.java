package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Business.UserBusiness;
import ca.usherbrooke.fgen.api.Entities.User;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/public/user")
public class UserPresentation {
    private final UserBusiness userBusiness;

    @Inject
    public UserPresentation(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    @GET()
    @Path("ping")
    public String ping() {
        return userBusiness.ping();
    }

    @GET
    @Path("all")
    public Response getAllUsers() {
        List<User> users = userBusiness.getAllUsers();
        return Response.ok(users).build();
    }

    @GET
    @Path("{id}")
    public Response getUserByUserId(@PathParam("id") long id) {
        User users = userBusiness.getUserByUserId(id);
        //return 404
        if(users == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(users).build();
    }
}