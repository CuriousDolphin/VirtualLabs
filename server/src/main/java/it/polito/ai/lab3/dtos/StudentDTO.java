package it.polito.ai.lab3.dtos;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class StudentDTO extends RepresentationModel<CourseDTO> {
    @CsvBindByName
    @NotBlank
    @NotNull
    private String id;
    @CsvBindByName
    @NotBlank
    @NotNull
    private String name;
    @CsvBindByName
    private String firstName;


}
