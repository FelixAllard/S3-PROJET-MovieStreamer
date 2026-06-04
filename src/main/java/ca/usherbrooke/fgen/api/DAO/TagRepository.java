package ca.usherbrooke.fgen.api.DAO;

import ca.usherbrooke.fgen.api.Entities.Tag;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TagRepository implements PanacheRepositoryBase<Tag, Integer> {

    public void deleteMovieTagLinksByTagId(int id) {
        getEntityManager()
                .createNativeQuery("DELETE FROM app.movie_tags WHERE tag_id = ?1")
                .setParameter(1, id)
                .executeUpdate();
    }
}
