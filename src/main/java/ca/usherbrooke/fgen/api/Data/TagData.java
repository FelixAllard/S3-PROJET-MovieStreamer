package ca.usherbrooke.fgen.api.Data;

import ca.usherbrooke.fgen.api.DAO.TagRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TagData {
    private final TagRepository tagRepository;

    public TagData(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public String ping(){
        return "pong!";
    }

    @Transactional
    public boolean deleteTagByTagId(int id) {
        tagRepository.deleteMovieTagLinksByTagId(id);
        return tagRepository.deleteById(id);
    }

}
