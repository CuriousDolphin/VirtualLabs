package it.polito.ai.virtualLabs.dtos;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TeacherDTO {
    @NotBlank
    @NotNull
    private String id;
    @NotBlank
    @NotNull
    private String name;
    @NotBlank
    @NotNull
    private String lastName;
    @NotBlank
    @NotNull
    @Email //TODO: in StudentDTO non c'è questa annotazione
    private String email;

}
