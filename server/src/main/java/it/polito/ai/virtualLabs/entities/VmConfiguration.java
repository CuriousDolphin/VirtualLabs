package it.polito.ai.virtualLabs.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @NotNull
    private int maxVcpusPerVm;
    @NotNull
    private int maxRamPerVm;
    @NotNull
    private int maxDiskPerVm;
    @NotNull
    private int maxRunningVms;
    @NotNull
    private int maxVms;
}
