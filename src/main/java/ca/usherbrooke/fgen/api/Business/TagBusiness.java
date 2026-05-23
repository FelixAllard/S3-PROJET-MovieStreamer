package ca.usherbrooke.fgen.api.Business;

import ca.usherbrooke.fgen.api.Data.TagData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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

}