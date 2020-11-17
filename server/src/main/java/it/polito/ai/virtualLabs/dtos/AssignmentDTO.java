package it.polito.ai.virtualLabs.dtos;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class AssignmentDTO {
    Long id;
    Timestamp releaseDate;
    Timestamp expiryDate;
    String content;
}
