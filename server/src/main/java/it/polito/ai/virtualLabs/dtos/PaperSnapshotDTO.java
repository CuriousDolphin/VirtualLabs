package it.polito.ai.virtualLabs.dtos;

import lombok.Data;

import java.io.InputStream;
import java.sql.Timestamp;

@Data
public class PaperSnapshotDTO {
    Long id;
    String content;
    String type;
    Timestamp submissionDate;
}
