package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Business.TagBusiness;
import ca.usherbrooke.fgen.api.Business.UserService;
import ca.usherbrooke.fgen.api.Entities.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;
import jakarta.ws.rs.PathParam;


@Path("/api/tag")
@RolesAllowed({"admin"})
public class TagPresentation {

    private final TagBusiness tagBusiness;
    private final UserService userService;

    @Inject
    public TagPresentation(TagBusiness tagBusiness, UserService userService) {
        this.tagBusiness = tagBusiness;
        this.userService = userService;
    }

    @GET()
    @Path("ping")
    @PermitAll
    public String ping() {
        return tagBusiness.ping();
    }
    
    @GET
    @Path("all")
    @PermitAll
    public Response getAllTags() {
        List<Tag> tags = tagBusiness.getAllTags();
        return Response.ok(tags).build();
    }

    @POST
    @Path("/")
    @RolesAllowed({"admin"})
    public Response postTag(Tag tag) {
        Tag created = tagBusiness.postTag(tag);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({"admin"})
    public Response updateTagByTagId(@PathParam("id") int id, Tag updatedTag) {
        Tag tag = tagBusiness.updateTagByTagId(id, updatedTag);
        return Response.ok(tag).build(); // 200
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"admin"})
    public Response deleteTagByTagId(@PathParam("id") int id) {
        boolean isDeleted = tagBusiness.deleteTagByTagId(id);

        if (isDeleted) {
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
