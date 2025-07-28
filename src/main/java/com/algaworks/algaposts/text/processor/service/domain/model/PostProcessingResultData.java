package com.algaworks.algaposts.text.processor.service.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PostProcessingResultData {

    private UUID postId;
    private Integer wordCount;
    private Double calculatedValue;
}
