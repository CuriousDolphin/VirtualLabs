package it.polito.ai.virtualLabs;

import lombok.Data;

import javax.validation.constraints.*;
import java.util.List;


@Data
public class TeamProposal{
    @NotBlank
    @NotNull
    @NotEmpty
    String name;
    @NotNull
    List<String> members; //students id
    @NotBlank
    @NotNull
    @NotEmpty
    String owner;
    @NotNull
    @Min(1)
    @Max(30)
    Integer daysTimeout;
}