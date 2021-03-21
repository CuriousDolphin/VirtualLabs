package it.polito.ai.virtualLabs.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;
@Data
public class FormPaperSnapshotDataDTO {
    @NotNull
    PaperSnapshotDTO papersnapshot;
    Integer vote;
    Boolean toReview;
}
