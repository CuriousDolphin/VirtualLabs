package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import org.hibernate.mapping.Join;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
public class PaperSnapshot {
    @Id
    @GeneratedValue
    Long id;
    String content;
    Timestamp submissionDate;
    @ManyToOne
    @JoinColumn(name = "paper_id")
    Paper paper;

}
