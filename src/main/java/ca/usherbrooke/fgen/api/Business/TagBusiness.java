package ca.usherbrooke.fgen.api.Business;

import ca.usherbrooke.fgen.api.Data.TagData;
import ca.usherbrooke.fgen.api.Entities.Tag;
import ca.usherbrooke.fgen.api.Utils.ExceptionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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
        if (tag.name == null)
            ExceptionUtils.throwException(400, "Tag Name Null");
        if (tag.name.isEmpty())
            ExceptionUtils.throwException(422, "Tag Name Cannot Be Empty");

        return tagData.postTag(tag);
    }

    public List<Tag> getAllTags(){
        return tagData.getAllTags();
    }

    public Tag getTagByName(String name) {
        if (name == null)
            ExceptionUtils.throwException(400, "Tag Name Null");
        if (name.isEmpty())
            ExceptionUtils.throwException(422, "Tag Name Cannot Be Empty");

        Tag tag = tagData.getTagByName(name);
        if (tag == null)
            ExceptionUtils.throwException(404, "Tag Not Found");

        return tag;
    }

    public boolean deleteTagByTagId(int id) {
        if (id < 0)
            ExceptionUtils.throwException(422, "Unprocessable ID: ID cannot be negative");
        return tagData.deleteTagByTagId(id);

    }

    public Tag updateTagByTagId(int id, Tag updatedTag) {
        if (id < 0)
            ExceptionUtils.throwException(422, "Unprocessable ID: ID cannot be negative");
        if (updatedTag == null)
            ExceptionUtils.throwException(400, "Unprocessable Tag: Tag cannot be null");
        if (updatedTag.name == null)
            ExceptionUtils.throwException(400, "Tag Name Null");
        if (updatedTag.name.isEmpty())
            ExceptionUtils.throwException(422, "Tag Name Cannot Be Empty");

        // TODO: enlever cette validation quand la colonne name sera VARCHAR(255) dans la BD
        // Actuellement name est TEXT (pas de limite) car Hibernate mappe String → TEXT par défaut
        if (updatedTag.name.length() > 255)
            ExceptionUtils.throwException(422, "Tag Name Too Long");

        Tag tag = tagData.updateTagByTagId(id, updatedTag);
        return tag;
    }
}
