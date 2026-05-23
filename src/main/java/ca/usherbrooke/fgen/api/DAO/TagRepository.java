package ca.usherbrooke.fgen.api.DAO;

import ca.usherbrooke.fgen.api.Entities.Tag;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TagRepository implements PanacheRepository<Tag> {

}
