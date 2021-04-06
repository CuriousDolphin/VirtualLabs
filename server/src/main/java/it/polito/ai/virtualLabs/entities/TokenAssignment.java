package it.polito.ai.virtualLabs.entities;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.sql.Timestamp;

@Builder
@Entity
@Data
public class TokenAssignment {
    @Id
    String id;

    @OneToOne
    @JoinColumn(name = "assignment_id")
    Assignment assignment;

    //String studentId;
    Timestamp expiryDate;
}
