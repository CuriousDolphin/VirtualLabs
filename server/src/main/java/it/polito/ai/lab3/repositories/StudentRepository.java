package it.polito.ai.lab3.repositories;

import it.polito.ai.lab3.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository <Student,String> {
    Optional<Student> findByIdIgnoreCase(String id);
}
