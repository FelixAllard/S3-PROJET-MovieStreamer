package ca.usherbrooke.fgen.api.Business;

import ca.usherbrooke.fgen.api.Data.TagData;
import ca.usherbrooke.fgen.api.Entities.Tag;
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

    public List<Tag> getAllTags(){
        return tagData.getAllTags();
    }

}
    public boolean deleteTagByTagId(int id) {
        return tagData.deleteTagByTagId(id);
    }

}
