package it.polito.ai.virtualLabs.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
public class Assignment {
    @Id
    @GeneratedValue
    Long id;
    Timestamp releaseDate;
    Timestamp expiryDate;
    String content;
    @ManyToOne
    @JoinColumn(name = "course_id")
    Course course;
    @OneToMany(mappedBy = "assignment")
    List<Paper> papers;
}
