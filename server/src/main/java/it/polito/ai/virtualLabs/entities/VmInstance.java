package it.polito.ai.virtualLabs.entities;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Data
@Entity
public class VmInstance {

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
    private int state;
    @NotEmpty
    private int countVcpus;
    @NotEmpty
    private int countRam;
    @NotEmpty
    private int countDisks;

    private String owner;

}
