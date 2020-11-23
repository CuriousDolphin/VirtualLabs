package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.Team;
import it.polito.ai.virtualLabs.entities.TokenTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TokenTeamRepository extends JpaRepository<TokenTeam, String> {
    @Query("SELECT t FROM TokenTeam t WHERE t.expiryDate<:time")
    List<TokenTeam> findAllByExpiryBefore(Timestamp time); //per selezionare quelli scaduti

    @Query("SELECT t FROM TokenTeam t WHERE t.team.id=:teamId")
    List<TokenTeam> findAllByTeamId(Long teamId);

    boolean existsByTeamAndStudentId(Team team,String studentId);

    TokenTeam getByTeamAndStudentId(Team team,String studentId);


    List<TokenTeam> findAllByStudentId(String studentId);
}
