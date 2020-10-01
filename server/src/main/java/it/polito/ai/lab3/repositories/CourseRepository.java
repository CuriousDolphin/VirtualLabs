package it.polito.ai.lab3.repositories;

import it.polito.ai.lab3.entities.Course;
import it.polito.ai.lab3.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course,String> {

    Optional<Course> findByNameIgnoreCase(String name);




    @Query("SELECT s FROM Student s INNER JOIN s.teams t INNER JOIN t.course c WHERE c.name=:courseName")
    List<Student> getStudentsInTeams(String courseName);

    @Query("SELECT s FROM Student s INNER JOIN s.courses c WHERE c.name=:courseName  AND s.id NOT IN (SELECT s.id FROM Student s INNER JOIN s.teams t INNER JOIN t.course c WHERE c.name=:courseName)")
    List<Student> getStudentsNotInTeams(String courseName);

}
