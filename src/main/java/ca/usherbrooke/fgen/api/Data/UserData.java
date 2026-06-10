package ca.usherbrooke.fgen.api.Data;

import ca.usherbrooke.fgen.api.DAO.MovieRepository;
import ca.usherbrooke.fgen.api.DAO.UserRepository;
import ca.usherbrooke.fgen.api.DAO.WatchMovieUserRepository;
import ca.usherbrooke.fgen.api.Entities.Movie;
import ca.usherbrooke.fgen.api.Entities.User;
import ca.usherbrooke.fgen.api.Entities.WatchMovieUser;
import ca.usherbrooke.fgen.api.Utils.ExceptionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;


import java.util.List;

@ApplicationScoped
public class UserData {
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final WatchMovieUserRepository watchMovieUserRepository;

    public UserData(UserRepository userRepository, MovieRepository movieRepository, WatchMovieUserRepository watchMovieUserRepository) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.watchMovieUserRepository = watchMovieUserRepository;
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
    public WatchMovieUser postRatingByUserAndMovieID(long userId, long movieId, int rating) {

        User user = userRepository.findById(userId);
        if(user == null)
            ExceptionUtils.throwException(404, "User Not Found");

        WatchMovieUser watchMovieUser = user.watchedMovieUsers.stream()
                .filter(wmu -> wmu.movie.id == movieId)
                .findAny()
                .orElse(null);

        if(watchMovieUser != null && watchMovieUser.rating != null)
            ExceptionUtils.throwException(409, "Rating Already Exists");

        if(watchMovieUser == null) {
            Movie movie = movieRepository.findById(movieId);
            if(movie == null)
                ExceptionUtils.throwException(404, "Movie Not Found");

            watchMovieUser = new WatchMovieUser();
            watchMovieUser.setUser(user);
            watchMovieUser.setMovie(movie);
            watchMovieUser.setSaved(false);
            watchMovieUser.setStatus(null);
            watchMovieUserRepository.persist(watchMovieUser);
        }

        watchMovieUser.setRating(rating);
        return watchMovieUser;
    }
}
