package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Business.TagBusiness;
import ca.usherbrooke.fgen.api.Entities.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.util.List;
import jakarta.ws.rs.PathParam;

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

    @GET
    @Path("all")
    public Response getAllTags() {
        List<Tag> tags = tagBusiness.getAllTags();
        return Response.ok(tags).build();
    }
      
    @DELETE
    @Path("{id}")
    public Response deleteTagByTagId(@PathParam("id") int id) {
        boolean isDeleted = tagBusiness.deleteTagByTagId(id);

        if (isDeleted) {
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
