package it.polito.ai.virtualLabs.dtos;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
public class EnrolledStudentDTO {
    @NotBlank
    @NotNull
    private String id;
    @NotBlank
    @NotNull
    private String name;
    private String lastName;
    @NotBlank
    @NotNull
    private String email;

    private String teamName;

}
