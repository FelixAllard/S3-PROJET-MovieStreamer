package ca.usherbrooke.fgen.api.Business;

import ca.usherbrooke.fgen.api.Data.WatchMovieUserData;
import ca.usherbrooke.fgen.api.Entities.Movie;
import ca.usherbrooke.fgen.api.Entities.WatchMovieUser;
import ca.usherbrooke.fgen.api.Entities.MovieStatus;
import ca.usherbrooke.fgen.api.Entities.User;
import ca.usherbrooke.fgen.api.Utils.ExceptionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class WatchMovieUserBusiness {

    private final WatchMovieUserData watchMovieUserData;
    private final UserBusiness userBusiness;
    private final MovieBusiness movieBusiness;

    @Inject
    public WatchMovieUserBusiness(WatchMovieUserData watchMovieUserData, UserBusiness userBusiness, MovieBusiness movieBusiness) {
        this.watchMovieUserData = watchMovieUserData;
        this.userBusiness = userBusiness;
        this.movieBusiness = movieBusiness;
    }

    private void validateUserExists(long userId) {
        if (userId <= 0) {
            ExceptionUtils.throwException(422, "Invalid User ID");
        }
        if (userBusiness.getUserByUserId(userId) == null) {
            ExceptionUtils.throwException(404, "User does not exist in the database (has been deleted or was never created)");
        }
    }

    public List<WatchMovieUser> getUserSavedListByUserId(long userId) {
        validateUserExists(userId);
        return watchMovieUserData.getSavedListByUserId(userId);
    }

    public List<Movie> getUserSavedMoviesByUserId(long userId) {
        return getUserSavedListByUserId(userId)
                .stream()
                .map(WatchMovieUser::getMovie)
                .toList();
    }

    public List<WatchMovieUser> getUserWatchedMoviesByUserId(long userId) {
        validateUserExists(userId);
        return watchMovieUserData.getByUserIdAndStatus(userId, MovieStatus.WATCHED);
    }

    public List<WatchMovieUser> getUserStartedMoviesByUserId(long userId) {
        validateUserExists(userId);
        return watchMovieUserData.getByUserIdAndStatus(userId, MovieStatus.WATCHING);
    }

    public Integer getUserRatingByUserIdAndMovieId(long userId, long movieId) {
        validateUserExists(userId);

        if (movieId <= 0) {
            ExceptionUtils.throwException(400, "Invalid Movie ID");
        }

        var interactionOpt = watchMovieUserData.getByUserIdAndMovieId(userId, movieId);

        if (interactionOpt.isEmpty()) {
            ExceptionUtils.throwException(404, "No history found for this user and movie");
        }

        WatchMovieUser interaction = interactionOpt.get();

        if (interaction.getRating() == null) {
            ExceptionUtils.throwException(404, "Movie has not been rated by this user yet");
        }

        return interaction.getRating();
    }

    public boolean isMovieSavedByUserIdAndMovieId(long userId, long movieId) {
        validateUserExists(userId);
        validateMovieId(movieId);
        return watchMovieUserData.isMovieSavedByUserIdAndMovieId(userId, movieId);
    }

    public WatchMovieUser updateSavedStatus(long userId, long movieId, boolean saved) {
        User user = userBusiness.getUserByUserId(userId);
        if (user == null) {
            ExceptionUtils.throwException(404, "User does not exist in the database (has been deleted or was never created)");
        }

        Movie movie = validateMovieId(movieId);
        return watchMovieUserData.updateSavedStatus(user, movie, saved);
    }

    private Movie validateMovieId(long movieId) {
        if (movieId <= 0) {
            ExceptionUtils.throwException(400, "Invalid Movie ID");
        }

        return movieBusiness.getMovieByMovieId(movieId);
    }

    public List <Movie> getUserMovieRecommendationByUserId(long userId){
        validateUserExists(userId);
        return watchMovieUserData.getUserMovieRecommendationByUserId(userId);
    }

}
