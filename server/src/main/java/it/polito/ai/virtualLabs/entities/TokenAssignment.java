package it.polito.ai.virtualLabs.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenAssignment {
    @Id
    @GeneratedValue
    Long id;

    @OneToOne
    @JoinColumn(name = "assignment_id")
    Assignment assignment;

    //String studentId;
    Timestamp expiryDate;
}
