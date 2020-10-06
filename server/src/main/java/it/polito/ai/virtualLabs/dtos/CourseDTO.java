package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CourseDTO extends RepresentationModel<CourseDTO> {

    @NotBlank
    @NotNull
    private String name;
    @NotBlank
    @NotNull
    private String acronym;
    @Min(0)
    @Max(300)
    private int min;
    @Min(0)
    @Max(300)
    private int max;

    private boolean enabled;



}
