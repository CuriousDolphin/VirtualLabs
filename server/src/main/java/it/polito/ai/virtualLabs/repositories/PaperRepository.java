package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.Assignment;
import it.polito.ai.virtualLabs.entities.Paper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaperRepository extends JpaRepository<Paper, Long> {

    List<Paper> findAllByAssignment_Id(Long assignmentId);
    List<Paper> findAllByAssignment_Course_NameAndStudent_Id(String courseName, String studentId);
    Paper findByAssignment_IdAndStudent_Id(Long assignmentId, String studentId);
}
