package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.Assignment;
import it.polito.ai.virtualLabs.entities.TokenAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TokenAssignmentRepository extends JpaRepository<TokenAssignment, Long> {
    @Query("SELECT t FROM TokenAssignment t WHERE t.expiryDate<:time")
    List<TokenAssignment> findAllByExpiryBefore(Timestamp time); //per selezionare quelli scaduti

    @Query("SELECT t FROM TokenAssignment t WHERE t.assignment.id=:assignmentId")
    List<TokenAssignment> findAllByAssignmentId(Long assignmentId);

    //boolean existsByAssignmentAndStudentId(Assignment assignment, String studentId);

    //TokenAssignment getByAssignmentAndStudentId(Assignment assignment,String studentId);

    //List<TokenAssignment> findAllByStudentId(String studentId);
}