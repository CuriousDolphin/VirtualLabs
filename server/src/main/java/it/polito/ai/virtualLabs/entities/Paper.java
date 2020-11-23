package it.polito.ai.virtualLabs.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
public class Paper {
    @Id
    @GeneratedValue
    private Long id;
    private Integer status;
    private Integer vote;
    private Timestamp lastUpdateTime;
    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @OneToMany(mappedBy = "paper")
    private List<PaperSnapshot> paperSnapshots = new ArrayList<>();

    public void addPaperSnapshot(PaperSnapshot paperSnapshot) {
        if(!paperSnapshot.getPaper().equals(this)) paperSnapshot.setPaper(this);
        if(!paperSnapshots.contains(paperSnapshot)) paperSnapshots.add(paperSnapshot);
    }

    public void removeSnapshot(PaperSnapshot paperSnapshot) {
        if(paperSnapshot.getPaper().equals(this)) paperSnapshot.setPaper(null);
        if(paperSnapshots.contains(paperSnapshot)) paperSnapshots.remove(paperSnapshot);
    }
}
