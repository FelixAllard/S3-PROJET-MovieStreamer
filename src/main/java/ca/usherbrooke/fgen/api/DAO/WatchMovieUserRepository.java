package ca.usherbrooke.fgen.api.DAO;

import ca.usherbrooke.fgen.api.Entities.WatchMovieUser;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WatchMovieUserRepository implements PanacheRepository<WatchMovieUser> {
}
