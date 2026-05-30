package ca.usherbrooke.fgen.api.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "watch_movie_user", schema = "app")
public class WatchMovieUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    public Long movieId;

    @Enumerated(EnumType.STRING)
    public MovieStatus status;

    public boolean saved;
}