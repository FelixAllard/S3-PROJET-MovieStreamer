package ca.usherbrooke.fgen.api.Business;

import ca.usherbrooke.fgen.api.Data.TagData;
import ca.usherbrooke.fgen.api.Entities.Tag;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.WebApplicationException;

import java.util.List;

@ApplicationScoped
public class TagBusiness {
    private final TagData tagData;

    @Inject
    public TagBusiness(TagData tagData) {
        this.tagData = tagData;
    }

    public String ping() {
        return tagData.ping();
    }

    public Tag postTag(Tag tag) {
        if(tag.name.isEmpty() || tag.name.length() < 1 || tag.name == null) {throw new WebApplicationException("Name is NULL or empty.", 400);}
        return tagData.postTag(tag);
    }

    public List<Tag> getAllTags(){
        return tagData.getAllTags();
    }

    public boolean deleteTagByTagId(int id) {
        if(id<0)
            throw new WebApplicationException(
                    Response.status(422)
                            .entity("Unprocessable ID: ID cannot be negative")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );

        return tagData.deleteTagByTagId(id);

    }

    public Tag updateTagByTagId(int id, Tag updatedTag) {
        if(id<0)
            throw new WebApplicationException(
                    Response.status(422)
                            .entity("Unprocessable ID: ID cannot be negative")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        if(updatedTag==null)
            throw new WebApplicationException(
                    Response.status(422)
                            .entity("Unprocessable Tag: Tag cannot be null")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        if(updatedTag.name==null)
            throw new WebApplicationException(
                    Response.status(422)
                            .entity("Tag Name Null")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );

        if(updatedTag.name.isEmpty())
            throw new WebApplicationException(
                    Response.status(422)
                            .entity("Incorrect Tag Name: Tag name cannot be empty")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );

        if(updatedTag.name.length()>255)
            throw new WebApplicationException(
                    Response.status(400)
                            .entity("Tag Name Too Long")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );

        Tag tag = tagData.updateTagByTagId(id, updatedTag);
        return tag;
    }
}