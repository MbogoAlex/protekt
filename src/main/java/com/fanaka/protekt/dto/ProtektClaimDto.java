package com.fanaka.protekt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ProtektClaimDto {
    private Long id;
    private String incident;
    private String dateOfIncident;
    private String timeOfIncident;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<FileDto> files;
}
