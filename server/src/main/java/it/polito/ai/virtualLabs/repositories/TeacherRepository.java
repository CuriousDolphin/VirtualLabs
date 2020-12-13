package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.Student;
import it.polito.ai.virtualLabs.entities.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher,String> {
    Optional<Teacher> findByIdIgnoreCase(String id);
    Optional<Teacher> findByEmailIgnoreCase(String email);

}
