package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.VmConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VmConfigurationRepository extends JpaRepository<VmConfiguration,Long> {
}
