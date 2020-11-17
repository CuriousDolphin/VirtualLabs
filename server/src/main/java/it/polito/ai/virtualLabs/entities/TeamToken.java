package it.polito.ai.virtualLabs.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;

@Entity
@Data
public class TeamToken {
    @Id
    String id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    Team team;

    String studentId;
    Timestamp expiryDate;
}
