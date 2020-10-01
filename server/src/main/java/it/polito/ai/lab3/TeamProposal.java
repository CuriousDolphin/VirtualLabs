package it.polito.ai.lab3;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class TeamProposal{
    @NotBlank
    @NotNull
    @NotEmpty
    String name;
    @NotNull
    List<String> members;
}