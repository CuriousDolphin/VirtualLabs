package it.polito.ai.virtualLabs.dtos;

import lombok.Data;

@Data
public class VmModelDTO {
    private Long id;
    private String image;
    private int maxVcpus;
    private int maxRam;
    private int maxDisk;
    private int maxRunningVms;
    private int maxVms;
    private Long courseId;
}
