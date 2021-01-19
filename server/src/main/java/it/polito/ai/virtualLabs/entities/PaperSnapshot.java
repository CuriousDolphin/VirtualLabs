package it.polito.ai.virtualLabs.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaperSnapshot {
    @Id
    @GeneratedValue
    private Long id;
    @Lob
    @Column(columnDefinition="MEDIUMBLOB")
    private byte[] content;
    private Timestamp submissionDate;
    @ManyToOne
    @JoinColumn(name = "paper_id")
    private Paper paper;

    public void setPaper(Paper paper) {
        if (paper == null) {
            if (this.paper != null) {
                this.paper.removePaperSnapshot(this);
            }
            this.paper = null;
        } else {
            this.paper = paper;
            this.paper.addPaperSnapshot(this);
        }
    }
}
