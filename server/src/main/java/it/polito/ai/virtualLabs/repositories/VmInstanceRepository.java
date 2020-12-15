package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.Course;
import it.polito.ai.virtualLabs.entities.Team;
import it.polito.ai.virtualLabs.entities.VmInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VmInstanceRepository extends JpaRepository<VmInstance,Long> {

    List<VmInstance> getVmInstancesByTeam(Team team);

    int countDistinctByTeamAndStateEquals(Team team, int state);

    @Query("SELECT vm FROM VmInstance vm WHERE vm.team.course=:courseName")
    List<VmInstance> getVmInstancesByCourse(Course courseName);

    @Query("SELECT COUNT(distinct vm) FROM VmInstance vm GROUP BY vm.team")
    int getMaxVmsPerTeam();

}
