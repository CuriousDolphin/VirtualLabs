package it.polito.ai.virtualLabs;

import it.polito.ai.virtualLabs.dtos.UserDTO;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserProposal {
    @NotBlank
    @NotNull
    @Email
    private String username;
    @NotBlank
    @NotNull
    private String password;
    @NotBlank
    @NotNull
    private String confirmPassword;
    @NotBlank
    @NotNull
    private String name;
    @NotBlank
    @NotNull
    private String lastName;
}
