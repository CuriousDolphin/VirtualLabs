package it.polito.ai.virtualLabs.dtos;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PaperSnapshotDTO {
    Long id;
    String content;
    Timestamp submissionDate;
}
