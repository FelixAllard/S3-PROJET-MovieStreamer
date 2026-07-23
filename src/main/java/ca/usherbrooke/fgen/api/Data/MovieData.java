package ca.usherbrooke.fgen.api.Data;


import ca.usherbrooke.fgen.api.DAO.MovieRepository;
import ca.usherbrooke.fgen.api.DAO.TagRepository;
import ca.usherbrooke.fgen.api.Entities.Movie;
import ca.usherbrooke.fgen.api.Entities.Tag;
import ca.usherbrooke.fgen.api.Entities.WatchMovieUser;
import ca.usherbrooke.fgen.api.Utils.ExceptionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.transaction.Transactional;
import lombok.Builder;
import org.hibernate.Hibernate;


import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MovieData {
    private final MovieRepository movieRepository;
    private final TagRepository tagRepository;

    public MovieData(MovieRepository movieRepository, TagRepository tagRepository) {
        this.movieRepository = movieRepository;
        this.tagRepository = tagRepository;

    }

    @Transactional
    public Movie importMovieFromJellyfinData(Movie newMovie, List<String> genreNames) {
        List<Tag> managedTags = new ArrayList<>();
        if (genreNames != null) {
            for (String genreName : genreNames) {
                Tag existingTag = tagRepository.find("name", genreName).firstResult();
                if (existingTag != null) {
                    managedTags.add(existingTag);
                } else {
                    Tag newTag = new Tag();
                    newTag.setName(genreName);
                    tagRepository.persist(newTag);
                    managedTags.add(newTag);
                }
            }
        }

        newMovie.setId(null);
        newMovie.setTags(managedTags);
        newMovie.setWatchedMovieUsers(null);

        movieRepository.persist(newMovie);
        Hibernate.initialize(newMovie.getTags());

        return newMovie;
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
    public Movie postMovie(Movie movie) {
        if (movieRepository.count("title", movie.title) > 0) {
            ExceptionUtils.throwException(409, "Movie title already exists");
        }

        List<Tag> managedTags = new ArrayList<>();

        if (movie.getTags() != null) {
            managedTags = movie.getTags().stream()
                    .map(tag -> tagRepository.findById(tag.id))
                    .filter(java.util.Objects::nonNull)
                    .toList();
        }

        movie.setId(null);
        movie.setTags(managedTags);
        movie.setWatchedMovieUsers(null);

        movieRepository.persist(movie);

        Hibernate.initialize(movie.getTags());

        return movie;
    }

    public List<Movie> getMoviesByMovieTags(List<Integer> tagIds) {
        return movieRepository.findByTagIds(tagIds);
    }

    @Transactional
    public Movie updateMovieByMovieId(int id, Movie updatedMovie) {
        Movie movie = movieRepository.findById((long) id);
        if (movie == null)
            ExceptionUtils.throwException(404, "Movie Not Found");

        movie.setTitle(updatedMovie.title == null ? "" : updatedMovie.title);
        movie.setDescription(updatedMovie.description == null ? "" : updatedMovie.description);
        movie.setYear(updatedMovie.year);
        movie.setMovieLength(updatedMovie.movieLength);
        movie.setThumbnail(updatedMovie.thumbnail == null ? "" : updatedMovie.thumbnail);
        movie.setDirector(updatedMovie.director == null ? "" : updatedMovie.director);
        movie.setWriter(updatedMovie.writer == null ? "" : updatedMovie.writer);
        movie.setStudio(updatedMovie.studio == null ? "" : updatedMovie.studio);
        movie.setLanguage(updatedMovie.language == null ? "" : updatedMovie.language);
        movie.setStreamId(updatedMovie.streamId == null ? "" : updatedMovie.streamId);


        Hibernate.initialize(movie.getWatchedMovieUsers()); // Add this line
        // Resolve managed Tag entities from DB before associating
        if (updatedMovie.getTags() != null) {
            List<Tag> managedTags = updatedMovie.getTags().stream()
                    .map(t -> tagRepository.findById( t.id))
                    .filter(t -> t != null)
                    .collect(java.util.stream.Collectors.toList());

            movie.getTags().clear();
            movie.getTags().addAll(managedTags);
        }

        Hibernate.initialize(movie.getTags());

        return movie;
    }

    public List<Movie> searchMovies(List<Integer> tags, Integer yearMin, Integer yearMax,
                                    String language, String director, String studio,
                                    String writer, String title) {
        return movieRepository.searchMovies(tags, yearMin, yearMax, language, director, studio, writer, title);
    }

    public List<Movie> getMoviesByPartialTitle(String title) {
        return movieRepository.findMoviesByPartialName(title);
    }
}
