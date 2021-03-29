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
    private String title;
    private Timestamp releaseDate;
    private Timestamp expiryDate;
    @Lob
    @Column(columnDefinition="MEDIUMBLOB")
    private byte[] content;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    @OneToMany(mappedBy = "assignment")
    private List<Paper> papers = new ArrayList<>();

    public void addPaper(Paper paper) {
        if(papers == null) {
            papers = new ArrayList<>();
        }
        if(!papers.contains(paper)) papers.add(paper);
        if(!paper.getAssignment().equals(this)) paper.setAssignment(this);
    }

    public void removePaper(Paper paper) {
        if(papers.contains(paper)) papers.remove(paper);
        if(paper.getAssignment().equals(this)) paper.setAssignment(null);
    }

    public void setCourse(Course course) {
        if(course == null) {
            if(this.course != null) {
                this.course.removeAssignment(this);
            }
            this.course = null;
        } else { /* significa anche che course e' null */
            this.course = course;
            this.course.addAssignment(this);
        }
    }

    @Override
    public String toString(){
        return this.id+"_"+this.title+"_"+this.releaseDate+"_"+this.expiryDate+"_"+this.content+"_"+this.course.getName();
    }
}
