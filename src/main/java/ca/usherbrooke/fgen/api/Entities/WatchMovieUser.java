package ca.usherbrooke.fgen.api.Entities;

import jakarta.persistence.*;

@Entity
public class WatchMovieUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    //The user it belongs too
    @ManyToOne
    public User user;

    public Long movieId;

    @Enumerated(EnumType.STRING)
    public MovieStatus status;

    public boolean saved;


}
