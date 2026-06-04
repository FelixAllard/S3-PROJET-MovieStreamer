package ca.usherbrooke.fgen.api.Data;


import ca.usherbrooke.fgen.api.DAO.TagRepository;
import ca.usherbrooke.fgen.api.Entities.Tag;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TagData {
    private final TagRepository tagRepository;

    public TagData(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    public Tag postTag(Tag tag) {
        tagRepository.persist(tag);
        return tag;
    }

    public String ping(){
        return "pong!";
    }

    public boolean existsByName(String name) {
        return tagRepository.count("name", name) > 0;
    }
}
