package ca.usherbrooke.fgen.api.Data;

import ca.usherbrooke.fgen.api.DAO.WatchMovieUserRepository;
import ca.usherbrooke.fgen.api.Entities.WatchMovieUser;
import ca.usherbrooke.fgen.api.Entities.MovieStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class WatchMovieUserData {
    private final WatchMovieUserRepository watchMovieUserRepository;

    @Inject
    public WatchMovieUserData(WatchMovieUserRepository watchMovieUserRepository) {
        this.watchMovieUserRepository = watchMovieUserRepository;
    }

    // Might eventually need to use the mapper for all the sql ?
    public List<WatchMovieUser> getSavedListByUserId(long userId) {
        return watchMovieUserRepository.list("user.id = ?1 and saved = true", userId);
    }

    public List<WatchMovieUser> getByUserIdAndStatus(long userId, MovieStatus status) {
        return watchMovieUserRepository.list("user.id = ?1 and status = ?2", userId, status);
    }

    public Optional<WatchMovieUser> getByUserIdAndMovieId(long userId, long movieId) {
        return watchMovieUserRepository.find("user.id = ?1 and movie.id = ?2", userId, movieId)
                .firstResultOptional();
    }
}