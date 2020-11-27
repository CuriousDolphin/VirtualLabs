package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface TeamRepository extends JpaRepository<Team,Long> {

    Team getByName(String teamName);
}
