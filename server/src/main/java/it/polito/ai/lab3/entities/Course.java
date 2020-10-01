package it.polito.ai.lab3.entities;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Course {

    @Id
    @Column(name="name")
    @ColumnTransformer(read = "UPPER(name)")
    private String name;
    private int min;
    private int max;
    private boolean enabled;

    @ManyToMany(mappedBy = "courses")
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Team> teams;

    public void addStudent(Student s){
        if(!this.students.contains(s))  this.students.add(s);
        if(!s.getCourses().contains(this))  s.addCourse(this);
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
        System.out.println("SETTINGG NAME"+name);
        this.name=name.toUpperCase();
    }
}
