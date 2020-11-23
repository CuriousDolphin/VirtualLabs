package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.PaperSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaperSnapshotRepository extends JpaRepository<PaperSnapshot, Long> {
}
