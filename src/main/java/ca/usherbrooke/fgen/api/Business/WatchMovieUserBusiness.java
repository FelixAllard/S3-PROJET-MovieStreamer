package ca.usherbrooke.fgen.api.Business;

import ca.usherbrooke.fgen.api.Data.WatchMovieUserData;
import ca.usherbrooke.fgen.api.Entities.WatchMovieUser;
import ca.usherbrooke.fgen.api.Entities.MovieStatus;
import ca.usherbrooke.fgen.api.Utils.ExceptionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class WatchMovieUserBusiness {

    private final WatchMovieUserData watchMovieUserData;
    private final UserBusiness userBusiness;

    @Inject
    public WatchMovieUserBusiness(WatchMovieUserData watchMovieUserData, UserBusiness userBusiness) {
        this.watchMovieUserData = watchMovieUserData;
        this.userBusiness = userBusiness;
    }

    private void validateUserExists(long userId) {
        if (userId <= 0) {
            ExceptionUtils.throwException(400, "Invalid User ID");
        }
        if (userBusiness.getUserByUserId(userId) == null) {
            ExceptionUtils.throwException(404, "User does not exist in the database (has been deleted or was never created)");
        }
    }

    public List<WatchMovieUser> getUserSavedListByUserId(long userId) {
        validateUserExists(userId);
        return watchMovieUserData.getSavedListByUserId(userId);
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
}