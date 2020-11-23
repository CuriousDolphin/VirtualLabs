package it.polito.ai.virtualLabs.entities;

import lombok.Builder;
import lombok.Data;
import org.hibernate.mapping.Join;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Builder
public class PaperSnapshot {
    @Id
    @GeneratedValue
    private Long id;
    private String content;
    private Timestamp submissionDate;
    @ManyToOne
    @JoinColumn(name = "paper_id")
    private Paper paper;

}
