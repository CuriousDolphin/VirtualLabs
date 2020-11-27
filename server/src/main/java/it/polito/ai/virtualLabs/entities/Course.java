package it.polito.ai.virtualLabs.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue
    private Long id;
    @Column(name="name",unique = true)
    @ColumnTransformer(read = "UPPER(name)")
    private String name;
    @Column(unique = true)
    private String acronym;
    private int min;
    private int max;
    private boolean enabled;
    @ManyToMany(mappedBy = "courses")
    private List<Student> students = new ArrayList<>();
    @OneToMany(mappedBy = "course")
    private List<Team> teams;
    @OneToMany(mappedBy = "course")
    private List<Assignment> assignments;

    public void addStudent(Student s){
        if(!this.students.contains(s))  this.students.add(s);
        if(!s.getCourses().contains(this))  s.addCourse(this);
    }

    public void removeStudent(Student s){
        if(this.students.contains(s))  this.students.remove(s);
        if(s.getCourses().contains(this))  s.removeCourse(this);
    }

    public void addTeam(Team t){
        if(!this.teams.contains(t)) this.teams.add(t);
        if(!t.getCourse().equals(this)) t.setCourse(this);
    }

    public void removeTeam(Team t){
        this.teams.remove(t);
        if(t.getCourse().equals(this)) t.setCourse(null);

    }

    @Override
    public String toString(){
        return this.name+"_"+this.min+"_"+this.max;
    }


    /*public String getName(){
        System.out.println("getting NAME"+name);
        return this.name.toUpperCase();
    }*/
    public void setName(String name){
        System.out.println("SETTING NAME "+name);
        this.name=name.toUpperCase();
    }
}
