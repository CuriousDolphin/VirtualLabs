package it.polito.ai.virtualLabs.dtos;

import lombok.Data;

import java.util.List;

@Data
public class TeamDTO {
    private Long id;
    private String name;
    private int status;
    private List<StudentDTO> members;
    private  StudentDTO owner;
}
