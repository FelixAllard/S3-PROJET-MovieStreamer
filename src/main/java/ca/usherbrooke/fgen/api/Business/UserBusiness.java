package ca.usherbrooke.fgen.api.Business;

import ca.usherbrooke.fgen.api.Data.UserData;
import ca.usherbrooke.fgen.api.Entities.User;
import ca.usherbrooke.fgen.api.Entities.WatchMovieUser;
import ca.usherbrooke.fgen.api.Utils.ExceptionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@ApplicationScoped
public class UserBusiness {
    private final UserData userData;
    private final UserService userService;

    @Inject
    public UserBusiness(UserData userData, UserService userService) {
        this.userData = userData;
        this.userService = userService;
    }

    public String ping() {
        return userData.ping();
    }

    public List<User> getAllUsers(){
        return userData.getAllUsers();
    }

    public User getUserByUserId(long id)
    {
        if(id < 0){
            throw new WebApplicationException(
                    Response.status(422)
                            .entity("Unprocessable ID: ID cannot be negative")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }
        User users = userData.getUserByUserId(id);

        if(users == null){
            throw new WebApplicationException(
                    Response.status(404)
                            .entity("User not found")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }
        return userData.getUserByUserId(id);
    }

    public boolean deleteUserByUserId(long id) {
        if (id < 0)
            throw new WebApplicationException(
                    Response.status(422)
                            .entity("Unprocessable ID: ID cannot be negative")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        boolean isDeleted = userData.deleteUserByUserId(id);
        if (!isDeleted) {
            throw new WebApplicationException(
                    Response.status(404)
                            .entity("ID not found")
                            .type(MediaType.APPLICATION_JSON)
                            .build()
            );
        }
        return isDeleted;
    }

    public WatchMovieUser updateUserRatingByUserId(long userId, long movieId, int newRating) {
        if (userId <= 0 || movieId <= 0)
            ExceptionUtils.throwException(422, "UserId or MovieId is negative");
        if (newRating > 10 || newRating < 0)
            ExceptionUtils.throwException(400, "Rating does not respect 0 to 10 range");
        return userData.updateUserRatingByUserId(userId, movieId, newRating);
    }

    public User registerNewUser(String username, String email, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            ExceptionUtils.throwException(400, "Username or password cannot be blank.");
        }
        return userService.registerNewUser(username, email, password);
    }
}
