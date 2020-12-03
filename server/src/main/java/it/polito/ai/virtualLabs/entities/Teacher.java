package it.polito.ai.virtualLabs.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {

    @Id
    private String id;
    private String name;
    private String lastName;
    private String email;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name="teacher_course",
            joinColumns = @JoinColumn(name="teacher_id"),
            inverseJoinColumns = @JoinColumn(name="course_id") )
    List<Course> courses = new ArrayList<>();

    public void addCourse(Course course){
        if(!this.courses.contains(course)) this.courses.add(course);
        if(!course.getTeachers().contains(this)) course.addTeacher(this);
    }
}
