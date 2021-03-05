package it.polito.ai.virtualLabs.dtos;

import lombok.Data;

@Data
public class VmInstanceDTO {
    private Long id;
    private int state;
    private int countVcpus;
    private int countRam;
    private int countDisks;
    private String owner;
    private String creator;
    private String image;
    private Long teamId;
}
