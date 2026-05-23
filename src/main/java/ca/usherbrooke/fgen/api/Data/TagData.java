package ca.usherbrooke.fgen.api.Data;


import ca.usherbrooke.fgen.api.DAO.TagRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TagData {
    private final TagRepository tagRepository;

    public TagData(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }


    public String ping(){
        return "pong!";
    }

}
