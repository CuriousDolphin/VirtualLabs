package it.polito.ai.virtualLabs.dtos;

import lombok.Data;

@Data
public class VmConfigurationDTO {
    private Long id;
    private int maxVcpusPerVm;
    private int maxRamPerVm;
    private int maxDiskPerVm;
    private int maxRunningVms;
    private int maxVms;
}
