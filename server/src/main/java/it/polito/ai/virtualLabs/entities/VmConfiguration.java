package it.polito.ai.virtualLabs.entities;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Data
@Entity
public class VmConfiguration {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    Team team;

    @ManyToOne
    @JoinColumn(name = "VmModel_id")
    VmModel vmModel;

    @NotEmpty
    private int maxVcpusPerVm;
    @NotEmpty
    private int maxRamPerVm;
    @NotEmpty
    private int maxDisksPerVm;
    @NotEmpty
    private int maxRunningVms;
    @NotEmpty
    private int maxVms;
}
