package ca.usherbrooke.fgen.api.Entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "app_user", schema = "app")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    public String surname;
    public String email;

    @Column(name = "keycloak_id", unique = true)
    public String keycloakId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<WatchMovieUser> watchedMovies = new ArrayList<>();
}