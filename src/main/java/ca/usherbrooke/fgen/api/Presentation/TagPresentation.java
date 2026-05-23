package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Business.TagBusiness;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;



@Path("/public/tag")
public class TagPresentation {

    private final TagBusiness tagBusiness;

    @Inject
    public TagPresentation(TagBusiness tagBusiness) {
        this.tagBusiness = tagBusiness;
    }


    @GET()
    @Path("ping")
    public String ping() {
        return tagBusiness.ping();
    }

}
