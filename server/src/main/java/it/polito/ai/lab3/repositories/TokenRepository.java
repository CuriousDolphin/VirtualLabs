package it.polito.ai.lab3.repositories;

import it.polito.ai.lab3.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
    @Query("SELECT t FROM Token t WHERE t.expiryDate<:time")
    List<Token> findAllByExpiryBefore(Timestamp time); //per selezionare quelli scaduti

    @Query("SELECT t FROM Token t WHERE t.teamId=:teamId")
    List<Token> findAllByTeamId(Long teamId);
}
