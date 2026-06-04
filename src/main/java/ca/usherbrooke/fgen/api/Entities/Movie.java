package ca.usherbrooke.fgen.api.Entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "movie", schema = "app")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String title;
    public String description;

    @ManyToMany
    @JoinTable(
            name = "movie_tags",
            schema = "app",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    public List<Tag> tags = new ArrayList<>();

    public int year;
    public float movieLength;

    public String thumbnail;
    public String director;
    public String writer;
    public String studio;
    public String language;



}
