package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.VmInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VmInstanceRepository extends JpaRepository<VmInstance,Long> {
}
