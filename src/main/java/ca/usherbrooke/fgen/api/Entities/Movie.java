package ca.usherbrooke.fgen.api.Entities;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "movie", schema = "app")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String title;
    public String description;
    @ElementCollection
    public List<Long> tagIds;
    public int year;
    public float movieLength;

    public String thumbnail;
    public String director;
    public String writer;
    public String studio;
    public String language;



}
