package it.polito.ai.virtualLabs.entities;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Builder
@Data
@Entity
public class Teacher {

    @Id
    private String id;
    private String name;
    private String lastName;
    private String email;

}
