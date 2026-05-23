package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Buisness.TagBuisness;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;



@Path("/public/tag")
public class TagPresentation {

    private final TagBuisness tagBuisness;

    @Inject
    public TagPresentation(TagBuisness tagBuisness) {
        this.tagBuisness = tagBuisness;
    }


    @GET()
    @Path("ping")
    public String ping() {
        return tagBuisness.ping();
    }

}
