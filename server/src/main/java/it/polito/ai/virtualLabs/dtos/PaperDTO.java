package it.polito.ai.virtualLabs.dtos;

import it.polito.ai.virtualLabs.entities.Student;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
public class PaperDTO {
    Long id;
    Integer status;
    Integer vote;
    Timestamp lastUpdateTime;
    @NotNull
    StudentDTO student;
}
