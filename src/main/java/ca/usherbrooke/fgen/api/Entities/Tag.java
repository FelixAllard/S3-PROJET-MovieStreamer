package ca.usherbrooke.fgen.api.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tag", schema = "app")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String name;
}
