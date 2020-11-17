package it.polito.ai.virtualLabs.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    @ManyToOne
    @JoinColumn(name = "course_id")
    Course course;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "team_student",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    List<Student> members = new ArrayList<>();
    @OneToMany(mappedBy = "team")
    private List<VmInstance> vmInstances;
    @OneToMany(mappedBy = "team")
    private List<VmConfiguration> vmConfigurations;
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int status;

    public void setCourse(Course c) {
        if (c == null) {
            if(this.course!=null){
                this.course.getTeams().remove(this);
            }
            this.course = null;


        } else {
            this.course = c;
            if (!c.getTeams().contains(this)) c.addTeam(this);
        }
    }

    public void addMembers(Student s) {
        if (!this.members.contains(s)) this.members.add(s);
        if (!s.getTeams().contains(this)) s.addTeam(this);

    }

    public void removeMembers(Student s) {
        if (this.members.contains(s)) this.members.remove(this);
        if (s.getTeams().contains(this)) s.removeTeam(this);

    }


}
