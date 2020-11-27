package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.Course;
import it.polito.ai.virtualLabs.entities.VmModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VmModelRepository extends JpaRepository<VmModel,Long> {
    VmModel getByCourse(Course course);
}
