package ca.usherbrooke.fgen.api.DAO;

import ca.usherbrooke.fgen.api.Entities.Movie;
import ca.usherbrooke.fgen.api.Entities.Tag;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class MovieRepository implements PanacheRepository<Movie> {

    public List<Object[]> getRatingDistributionByMovieId(Long id) {
        return getEntityManager()
                .createQuery("SELECT wmu.rating, COUNT(wmu) FROM WatchMovieUser wmu WHERE wmu.movie.id = ?1 AND wmu.rating IS NOT NULL GROUP BY wmu.rating")
                .setParameter(1, id)
                .getResultList();
    }
    public List<Movie> findNewestMovies(int limit) {
        return getEntityManager().createQuery(
                        "SELECT m FROM Movie m ORDER BY m.year DESC",
                        Movie.class)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Movie> findByTagIds(List<Integer> tagIds) {
        return getEntityManager()
                .createQuery("SELECT DISTINCT m FROM Movie m JOIN m.tags t WHERE t.id IN :tagIds", Movie.class)
                .setParameter("tagIds", tagIds)
                .getResultList();
    }
}
