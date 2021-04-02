package it.polito.ai.virtualLabs.dtos;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class AssignmentDTO {
    Long id;
    String title;
    Timestamp releaseDate;
    Timestamp expiryDate;
    String content;
}
