package it.polito.ai.virtualLabs.dtos;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class StudentAssignmentDTO {
    Long id;
    String status;
    Timestamp releaseDate;
    Timestamp expiryDate;
    String content;
}