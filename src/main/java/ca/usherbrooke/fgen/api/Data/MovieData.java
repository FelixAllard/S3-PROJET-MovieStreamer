package ca.usherbrooke.fgen.api.Data;


import ca.usherbrooke.fgen.api.DAO.MovieRepository;
import ca.usherbrooke.fgen.api.DAO.TagRepository;
import ca.usherbrooke.fgen.api.Entities.Movie;
import ca.usherbrooke.fgen.api.Entities.Tag;
import ca.usherbrooke.fgen.api.Entities.WatchMovieUser;
import ca.usherbrooke.fgen.api.Utils.ExceptionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;


import java.util.List;

@ApplicationScoped
public class MovieData {
    private final MovieRepository movieRepository;


    public MovieData(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;

    }

    public String ping(){
        return "pong!";
    }

    public List<Movie> getAllMovies(){
        return movieRepository.listAll();
    }

    public Movie getMovieByMovieId(long id){
        return movieRepository.findById(id);
    }

    @Transactional
    public boolean deleteMovieByMovieId(long id) {
        return movieRepository.deleteById(id);
    }
    
    public Movie getMovieByMovieName(String name) {
        return movieRepository.find("title", name).firstResult();
    }

    public List<Object[]> getMovieRatingByMovieId(long id) {
        Movie movie = movieRepository.findById(id);

        if(movie == null)
            ExceptionUtils.throwException(404, "Movie Not Found");

        return movieRepository.getRatingDistributionByMovieId(id);
    }
    public List<Movie> getNewMovies(int number){

        return movieRepository.findNewestMovies(number);
    }
    @Transactional
    public Movie postMovie(Movie movie){
        if (movieRepository.count("title", movie.title) > 0)
            ExceptionUtils.throwException(409, "title Name Already Exists");

        movie.setId(null);
        movie.setTags(null);
        movie.setWatchedMovieUsers(null);

        movieRepository.persist(movie);
        return movie;
    }
}
