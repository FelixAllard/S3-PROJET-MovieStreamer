package ca.usherbrooke.fgen.api.Presentation;


import ca.usherbrooke.fgen.api.Business.UserBusiness;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

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
        return "Pong";
    }

}
