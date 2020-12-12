package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.PaperSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaperSnapshotRepository extends JpaRepository<PaperSnapshot, Long> {

    List<PaperSnapshot> findAllByPaper_Id(Long paperId);
}
