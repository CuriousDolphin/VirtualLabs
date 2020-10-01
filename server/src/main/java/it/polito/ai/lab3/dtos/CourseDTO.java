package it.polito.ai.lab3.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CourseDTO extends RepresentationModel<CourseDTO> {

    @NotBlank
    @NotNull

    private String name;
    private int min;
    private int max;
    private boolean enabled;



}
