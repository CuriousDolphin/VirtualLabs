package it.polito.ai.virtualLabs;

import it.polito.ai.virtualLabs.dtos.CourseDTO;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CourseProposal {
    private CourseDTO course;
    private List<String> userIds;
}
