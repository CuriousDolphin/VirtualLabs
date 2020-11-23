package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.Paper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaperRepository extends JpaRepository<Paper, Long> {
}
