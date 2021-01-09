package it.polito.ai.virtualLabs.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FormDataDTO {
    @NotNull
    PaperSnapshotDTO paperSnapshotDTO;
    Integer vote;
    Boolean toReview;
    String imgSource;

}
