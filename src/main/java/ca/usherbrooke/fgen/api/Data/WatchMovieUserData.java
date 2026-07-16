package ca.usherbrooke.fgen.api.Data;

import ca.usherbrooke.fgen.api.DAO.MovieRepository;
import ca.usherbrooke.fgen.api.DAO.WatchMovieUserRepository;
import ca.usherbrooke.fgen.api.Entities.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class WatchMovieUserData {
    private final WatchMovieUserRepository watchMovieUserRepository;
    private final MovieRepository movieRepository;

    @Inject
    public WatchMovieUserData(WatchMovieUserRepository watchMovieUserRepository, MovieRepository movieRepository) {
        this.watchMovieUserRepository = watchMovieUserRepository;
        this.movieRepository = movieRepository;
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

    public boolean isMovieSavedByUserIdAndMovieId(long userId, long movieId) {
        return watchMovieUserRepository.count("user.id = ?1 and movie.id = ?2 and saved = true", userId, movieId) > 0;
    }

    @Transactional
    public WatchMovieUser updateSavedStatus(User user, Movie movie, boolean saved) {
        WatchMovieUser watchMovieUser = getByUserIdAndMovieId(user.getId(), movie.getId())
                .orElse(null);

        if (watchMovieUser == null) {
            watchMovieUser = new WatchMovieUser(null, user, movie, MovieStatus.NOT_WATCHED, saved, null);
            watchMovieUserRepository.persist(watchMovieUser);
        } else {
            watchMovieUser.setSaved(saved);
        }

        return watchMovieUser;
    }

    public List<Movie> getUserMovieRecommendationByUserId(long userId){
        List<Movie> savedMovies = getSavedListByUserId(userId).stream()
                .map(w -> w.movie)
                .toList();

        if(savedMovies.isEmpty()){
            return List.of();
        }

        List<Long> savedMovieIds = savedMovies.stream()
                .map(movie -> movie.id)
                .toList();

        List<Integer> preferredTagIds = savedMovies.stream()
                .flatMap(movie -> movie.getTags().stream())
                .map(Tag::getId)
                .distinct()
                .toList();

        if(preferredTagIds.isEmpty()){
            return List.of();
        }

        return movieRepository.listAll().stream()
                .filter(movie -> !savedMovieIds.contains((movie.getId())))
                .filter(movie -> movie.getTags().stream()
                        .map(Tag::getId)
                        .anyMatch(preferredTagIds::contains))
                .toList();
    }
}
