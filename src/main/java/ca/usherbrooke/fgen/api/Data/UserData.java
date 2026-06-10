package ca.usherbrooke.fgen.api.Data;

import ca.usherbrooke.fgen.api.DAO.UserRepository;
import ca.usherbrooke.fgen.api.Entities.User;
import ca.usherbrooke.fgen.api.Entities.WatchMovieUser;
import ca.usherbrooke.fgen.api.Utils.ExceptionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;


import java.util.List;

@ApplicationScoped
public class UserData {
    private final UserRepository userRepository;

    public UserData(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //@Transactional NEEDED TO MODIFY DATABASE
    public String ping() {
        return "pong!";
    }

    public List<User> getAllUsers(){
        return userRepository.listAll();
    }

    public User getUserByUserId(Long id)
    {
        return userRepository.findById(id);
    }

    @Transactional
    public User updateUserRatingByUserId(long userId, long movieId, int newRating){
        User user = userRepository.findById(userId);

        if (user == null) ExceptionUtils.throwException(404, "User Not Found");

        WatchMovieUser watchMovieUser = user.watchedMovieUsers
                .stream()
                .filter(w -> w.movie.id.equals(movieId))
                .findFirst()
                .orElse(null);

        if (watchMovieUser == null)
            ExceptionUtils.throwException(404, "User has not watched this movie");

        watchMovieUser.setRating(newRating);
        return user;
    }
}