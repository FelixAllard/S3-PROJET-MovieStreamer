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

    public List<Movie> searchMovies(List<Integer> tags, Integer yearMin, Integer yearMax,
                                    String language, String director, String studio,
                                    String writer, String title) {
        StringBuilder query = new StringBuilder("SELECT DISTINCT m FROM Movie m");

        if (tags != null && !tags.isEmpty())
            query.append(" JOIN m.tags t");

        query.append(" WHERE 1=1");

        if (tags != null && !tags.isEmpty())
            query.append(" AND t.id IN :tags");
        if (yearMin != null)
            query.append(" AND m.year >= :yearMin");
        if (yearMax != null)
            query.append(" AND m.year <= :yearMax");
        if (language != null)
            query.append(" AND LOWER(m.language) LIKE :language");
        if (director != null)
            query.append(" AND LOWER(m.director) LIKE :director");
        if (studio != null)
            query.append(" AND LOWER(m.studio) LIKE :studio");
        if (writer != null)
            query.append(" AND LOWER(m.writer) LIKE :writer");
        if (title != null)
            query.append(" AND LOWER(m.title) LIKE :title");  // ← élastique avec LIKE

        var q = getEntityManager().createQuery(query.toString(), Movie.class);

        if (tags != null && !tags.isEmpty())
            q.setParameter("tags", tags);
        if (yearMin != null)
            q.setParameter("yearMin", yearMin);
        if (yearMax != null)
            q.setParameter("yearMax", yearMax);
        if (language != null)
            q.setParameter("language", "%" + language.toLowerCase() + "%");
        if (director != null)
            q.setParameter("director", "%" + director.toLowerCase() + "%");
        if (studio != null)
            q.setParameter("studio", "%" + studio.toLowerCase() + "%");
        if (writer != null)
            q.setParameter("writer", "%" + writer.toLowerCase() + "%");
        if (title != null)
            q.setParameter("title", "%" + title.toLowerCase() + "%");  // ← "in" → %in%

        return q.getResultList();
    }
}
