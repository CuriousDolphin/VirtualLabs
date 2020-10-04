package it.polito.ai.virtualLabs.dtos;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class StudentDTO extends RepresentationModel<StudentDTO> {
    @CsvBindByName
    @NotBlank
    @NotNull
    private String id;
    @CsvBindByName
    @NotBlank
    @NotNull
    private String name;
    @CsvBindByName
    private String lastName;
    @NotBlank
    @NotNull
    private String email;


}
