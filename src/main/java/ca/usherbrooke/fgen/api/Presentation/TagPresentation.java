package ca.usherbrooke.fgen.api.Presentation;

import ca.usherbrooke.fgen.api.Business.TagBusiness;
import ca.usherbrooke.fgen.api.Entities.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;


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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponse(responseCode = "201", description = "Tag créé avec succès")
    @APIResponse(responseCode = "400", description = "Nom du tag invalide ou vide")
    @APIResponse(responseCode = "409", description = "Tag déjà existant")
    public Response postTag(Tag tag) {
        Tag created = tagBusiness.postTag(tag);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
}
