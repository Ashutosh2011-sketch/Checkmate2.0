package com.checkmate.backend.dto;

import lombok.Data;

@Data
public class CompletionTrendDto {
    private String period;
    private Integer completed;
    private Integer total;
    private Double completionRate;
}