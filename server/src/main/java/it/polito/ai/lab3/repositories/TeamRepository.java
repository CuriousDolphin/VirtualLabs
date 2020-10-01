package it.polito.ai.lab3.repositories;

import it.polito.ai.lab3.entities.Student;
import it.polito.ai.lab3.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface TeamRepository extends JpaRepository<Team,Long> {
}
