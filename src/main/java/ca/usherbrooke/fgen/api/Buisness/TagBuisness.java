package ca.usherbrooke.fgen.api.Buisness;

import ca.usherbrooke.fgen.api.Data.TagData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TagBuisness {
    private final TagData tagData;


    @Inject
    public TagBuisness(TagData tagData) {
        this.tagData = tagData;
    }


    public String ping() {
        return tagData.ping();
    }

}