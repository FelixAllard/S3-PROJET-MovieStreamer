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

    public WatchMovieUser postRatingByUserAndMovieID(long userId, long movieId, int rating) {
        if(userId <= 0)
            ExceptionUtils.throwException(422, "Unprocessable User ID: User ID cannot be negative");
        if(movieId <= 0)
            ExceptionUtils.throwException(422, "Unprocessable Movie ID: Movie ID cannot be negative");
        if(rating < 0 || rating >10)
            ExceptionUtils.throwException(422, "Unprocessable Rating: Rating must be between 1 and 10");


        return userData.postRatingByUserAndMovieID(userId,movieId,rating);
    }


}
