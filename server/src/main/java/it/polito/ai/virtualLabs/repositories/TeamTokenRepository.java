package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.TeamToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TeamTokenRepository extends JpaRepository<TeamToken, String> {
    @Query("SELECT t FROM TeamToken t WHERE t.expiryDate<:time")
    List<TeamToken> findAllByExpiryBefore(Timestamp time); //per selezionare quelli scaduti

    @Query("SELECT t FROM TeamToken t WHERE t.team.id=:teamId")
    List<TeamToken> findAllByTeamId(Long teamId);

    List<TeamToken> findAllByStudentId(String studentId);
}
