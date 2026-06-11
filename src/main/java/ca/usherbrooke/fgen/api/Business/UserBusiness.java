package ca.usherbrooke.fgen.api.Business;

import ca.usherbrooke.fgen.api.Data.UserData;
import ca.usherbrooke.fgen.api.Entities.User;
import ca.usherbrooke.fgen.api.Entities.WatchMovieUser;
import ca.usherbrooke.fgen.api.Utils.ExceptionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class UserBusiness {
    private final UserData userData;

    @Inject
    public UserBusiness(UserData userData) {
        this.userData = userData;
    }

    public String ping() {
        return userData.ping();
    }

    public List<User> getAllUsers(){
        return userData.getAllUsers();
    }

    public User getUserByUserId(long id)
    {
        return userData.getUserByUserId(id);
    }

    public WatchMovieUser updateUserRatingByUserId(long userId, long movieId, int newRating) {
        if (userId <= 0 || movieId <= 0)
            ExceptionUtils.throwException(422, "UserId or MovieId is negative");
        if (newRating > 10 || newRating < 0)
            ExceptionUtils.throwException(400, "Rating does not respect 0 to 10 range");
        return userData.updateUserRatingByUserId(userId, movieId, newRating);
    }
}
