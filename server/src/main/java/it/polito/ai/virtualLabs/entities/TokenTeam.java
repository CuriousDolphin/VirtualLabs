package it.polito.ai.virtualLabs.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
public class TokenTeam {
    @Id
    String id;

    @OneToOne
    @JoinColumn(name = "team_id")
    Team team;

    String studentId;
    Timestamp expiryDate;
}
