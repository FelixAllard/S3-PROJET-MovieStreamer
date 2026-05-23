package ca.usherbrooke.fgen.api.TEACHER_EXAMPLE.business.service;

import ca.usherbrooke.fgen.api.DAO.TagRepository;
import ca.usherbrooke.fgen.api.Entities.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.List;

@Path("/public/hello")
public class
HelloService {

    @GET
    public String hello() {
        List<Tag> tags = new TagRepository().listAll();
        return "Hello RESTEasy Reactive";
    }
}
