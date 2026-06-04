package ca.usherbrooke.fgen.api.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "watch_movie_user", schema = "app")
public class WatchMovieUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties("watchedMovies")
    public User user;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    public Movie movie;

    @Enumerated(EnumType.STRING)
    public MovieStatus status;

    public boolean saved;
}