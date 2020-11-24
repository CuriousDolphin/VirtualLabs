package it.polito.ai.virtualLabs.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {
    @Id
    @GeneratedValue
    private Long id;
    private Timestamp releaseDate;
    private Timestamp expiryDate;
    private String content;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    @OneToMany(mappedBy = "assignment", fetch = FetchType.LAZY)
    private List<Paper> papers = new ArrayList<>();

    public void addPaper(Paper paper) {
        if(!paper.getAssignment().equals(this)) paper.setAssignment(this);
        if(!papers.contains(paper)) papers.add(paper);
    }

    public void removePaper(Paper paper) {
        if(paper.getAssignment().equals(this)) paper.setAssignment(null);
        if(papers.contains(paper)) papers.remove(paper);
    }

    @Override
    public String toString(){
        return this.id+"_"+this.releaseDate+"_"+this.expiryDate+"_"+this.content+"_"+this.course.getName();
    }
}
