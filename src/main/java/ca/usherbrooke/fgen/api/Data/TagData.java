package ca.usherbrooke.fgen.api.Data;


import ca.usherbrooke.fgen.api.DAO.TagRepository;
import ca.usherbrooke.fgen.api.Entities.Tag;
import ca.usherbrooke.fgen.api.Entities.User;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class TagData {
    private final TagRepository tagRepository;

    public TagData(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }


    public String ping(){
        return "pong!";
    }

    public List<Tag> getAllTags(){ return tagRepository.listAll();}
}
