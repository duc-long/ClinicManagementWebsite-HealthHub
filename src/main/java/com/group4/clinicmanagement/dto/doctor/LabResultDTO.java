package com.group4.clinicmanagement.dto.doctor;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class LabResultDTO {

    private Integer resultId;
    private Integer labRequestId;
    private String technicianName;
    private String resultText;
    private LocalDateTime createdAt;

    private List<LabImageDTO> images;
}