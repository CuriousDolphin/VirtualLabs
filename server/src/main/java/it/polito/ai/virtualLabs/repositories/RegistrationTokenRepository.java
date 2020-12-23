package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.RegistrationToken;
import it.polito.ai.virtualLabs.entities.TokenTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationTokenRepository extends JpaRepository<RegistrationToken,String> {
    Optional<RegistrationToken> findById(String id);
    @Query("SELECT t FROM RegistrationToken t WHERE t.expiryDate<:time")
    List<RegistrationToken> findAllByExpiryBefore(Timestamp time); //per selezionare quelli scaduti
}
