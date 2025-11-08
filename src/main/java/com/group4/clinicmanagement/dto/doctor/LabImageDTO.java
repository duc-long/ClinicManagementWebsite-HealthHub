package com.group4.clinicmanagement.dto.doctor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabImageDTO {

    private Integer imageId;
    private String filePath;
    private String description;
    private LocalDateTime createdAt;
}

