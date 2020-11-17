package it.polito.ai.virtualLabs.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
public class Paper {
    @Id
    @GeneratedValue
    Long id;
    Integer status;
    Integer vote;
    Timestamp lastUpdateTime;
    @ManyToOne
    @JoinColumn(name = "assignment_id")
    Assignment assignment;
    @ManyToOne
    @JoinColumn(name = "student_id")
    Student student;
    @OneToMany(mappedBy = "paper")
    List<PaperSnapshot> paperSnapshots;
}
