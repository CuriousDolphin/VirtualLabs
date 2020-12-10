package it.polito.ai.virtualLabs.dtos;

import lombok.Data;

@Data
public class VmModelDTO {
    private Long id;
    private String image;
    private int maxVcpusPerVm;
    private int maxRamPerVm;
    private int maxDiskPerVm;
    private int maxRunningVms;
    private int maxVms;
    private Long courseId;
}
