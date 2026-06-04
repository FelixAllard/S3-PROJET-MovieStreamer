package ca.usherbrooke.fgen.api.Data;

import ca.usherbrooke.fgen.api.DAO.TagRepository;
import ca.usherbrooke.fgen.api.Entities.Tag;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

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
  
    @Transactional
    public boolean deleteTagByTagId(int id) {
        tagRepository.deleteMovieTagLinksByTagId(id);
        return tagRepository.deleteById(id);
    }

}
