package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findAllByCourse_Name(String courseName);
}
