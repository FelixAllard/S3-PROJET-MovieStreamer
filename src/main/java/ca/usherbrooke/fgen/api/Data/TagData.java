package ca.usherbrooke.fgen.api.Data;

import ca.usherbrooke.fgen.api.DAO.TagRepository;
import ca.usherbrooke.fgen.api.Entities.Tag;
import ca.usherbrooke.fgen.api.Utils.ExceptionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@ApplicationScoped
public class TagData {
    private final TagRepository tagRepository;

    public TagData(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    public Tag postTag(Tag tag) {
        //On god jvais changer tous les count par un UNIQUE
        if (tagRepository.count("name", tag.name) > 0)
            ExceptionUtils.throwException(409, "Tag Name Already Exists");

        tagRepository.persist(tag);
        return tag;
    }


    public String ping(){
        return "pong!";
    }

    public List<Tag> getAllTags(){ return tagRepository.listAll();}

    public Tag getTagByName(String name) {
        return tagRepository.find("name", name).firstResult();
    }

    @Transactional
    public boolean deleteTagByTagId(int id) {
        tagRepository.deleteMovieTagLinksByTagId(id);
        return tagRepository.deleteById(id);
    }

    @Transactional
    public Tag updateTagByTagId(int id, Tag updatedTag) {
        Tag tag = tagRepository.findById(id);
        if (tag==null)
            ExceptionUtils.throwException(404, "Tag Not Found");
        if (tagRepository.count("name", updatedTag.name) > 0)
            ExceptionUtils.throwException(409, "Tag Name Already Exists");

        tag.setName(updatedTag.name);

        return tag;
    }

}
