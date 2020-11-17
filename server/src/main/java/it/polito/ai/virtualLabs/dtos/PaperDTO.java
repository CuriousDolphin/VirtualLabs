package it.polito.ai.virtualLabs.dtos;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PaperDTO {
    Long id;
    Integer status;
    Integer vote;
    Timestamp lastUpdateTime;
}
