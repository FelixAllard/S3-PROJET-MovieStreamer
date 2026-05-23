package ca.usherbrooke.fgen.api.Presentation;


import ca.usherbrooke.fgen.api.Buisness.TagBuisness;
import ca.usherbrooke.fgen.api.Buisness.UserBuisness;
import ca.usherbrooke.fgen.api.DAO.TagRepository;
import ca.usherbrooke.fgen.api.Entities.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.List;

@Path("/public/user")
public class UserPresentation {
    private final UserBuisness userBuisness;

    @Inject
    public UserPresentation(UserBuisness userBuisness) {
        this.userBuisness = userBuisness;
    }

    @GET()
    @Path("ping")
    public String ping() {
        return "Pong";
    }

}
