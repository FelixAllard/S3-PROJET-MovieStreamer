package ca.usherbrooke.fgen.api.DAO;

import ca.usherbrooke.fgen.api.Entities.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
}
