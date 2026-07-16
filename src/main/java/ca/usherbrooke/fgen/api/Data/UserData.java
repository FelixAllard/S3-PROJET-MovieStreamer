package ca.usherbrooke.fgen.api.Data;

import ca.usherbrooke.fgen.api.DAO.MovieRepository;
import ca.usherbrooke.fgen.api.DAO.UserRepository;
import ca.usherbrooke.fgen.api.Entities.Movie;
import ca.usherbrooke.fgen.api.Entities.MovieStatus;
import ca.usherbrooke.fgen.api.Entities.User;
import ca.usherbrooke.fgen.api.Entities.WatchMovieUser;
import ca.usherbrooke.fgen.api.Utils.ExceptionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;


import java.util.List;

@ApplicationScoped
public class UserData {
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;


    public UserData(UserRepository userRepository, MovieRepository movieRepository) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
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
    public WatchMovieUser updateUserRatingByUserId(long userId, long movieId, int newRating){
        User user = userRepository.findById(userId);

        if (user == null) ExceptionUtils.throwException(404, "User Not Found");

        WatchMovieUser watchMovieUser = user.watchedMovieUsers
                .stream()
                .filter(w -> w.movie.id.equals(movieId))
                .findFirst()
                .orElse(null);

        if (watchMovieUser == null) {
            Movie movie = movieRepository.findById(movieId);
            if (movie == null) ExceptionUtils.throwException(404, "Movie Not Found");
            watchMovieUser = new WatchMovieUser(0L,
                    user,
                    movie,
                    MovieStatus.NOT_WATCHED,
                    false,
                    newRating);
            user.watchedMovieUsers.add(watchMovieUser);
        }
        else {
            watchMovieUser.setRating(newRating);
        }

        return watchMovieUser;
    }
}
