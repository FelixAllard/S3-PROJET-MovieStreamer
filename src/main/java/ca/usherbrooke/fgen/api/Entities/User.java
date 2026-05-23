package ca.usherbrooke.fgen.api.Entities;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "user", schema = "app")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    public String surname;
    public String email;
    public List<Long> savedMovies;

}
