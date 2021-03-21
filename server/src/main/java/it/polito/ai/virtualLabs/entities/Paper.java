package it.polito.ai.virtualLabs.entities;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paper {
    @Id
    @GeneratedValue
    private Long id;
    private String status;
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
        if (!paperSnapshot.getPaper().equals(this)) paperSnapshot.setPaper(this);
        if (!paperSnapshots.contains(paperSnapshot)) paperSnapshots.add(paperSnapshot);
    }

    public void removePaperSnapshot(PaperSnapshot paperSnapshot) {
        if (paperSnapshot.getPaper().equals(this)) paperSnapshot.setPaper(null);
        if (paperSnapshots.contains(paperSnapshot)) paperSnapshots.remove(paperSnapshot);
    }

    public void setAssignment(Assignment assignment) {
        if (assignment == null) {
            if (this.assignment != null)
                this.assignment.removePaper(this);
            this.assignment = null;
        } else {
            this.assignment = assignment;
            this.assignment.addPaper(this);
        }
    }

    public void setStudent(Student student) {
        if (student == null) {
            if (this.student != null) {
                this.student.removePaper(this);
            }
            this.student = null;
        } else {
            this.student = student;
            this.student.addPaper(this);
        }
    }

    @Override
    public String toString() {
        return this.id +
                "_" +
                this.status +
                "_" +
                this.vote +
                "_" +
                this.lastUpdateTime +
                "_" +
                this.getAssignment().getId() +
                "_" +
                this.student.getName();
    }
}
