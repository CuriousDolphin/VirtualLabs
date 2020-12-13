package it.polito.ai.virtualLabs.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VmModel {

    @Id
    @GeneratedValue
    private Long id;

    @NotEmpty
    private String image;

    @OneToOne()
    @JoinColumn(name = "course_id")
    Course course;

}
