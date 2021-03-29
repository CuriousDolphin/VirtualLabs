package it.polito.ai.virtualLabs.dtos;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class StudentAssignmentDTO {
    Long id;
    String title;
    String status;
    Integer vote;
    Timestamp releaseDate;
    Timestamp expiryDate;
    String content;
}