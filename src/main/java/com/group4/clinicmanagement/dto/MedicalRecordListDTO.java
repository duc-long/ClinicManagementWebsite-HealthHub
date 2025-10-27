package com.group4.clinicmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@Data
@NoArgsConstructor
public class MedicalRecordListDTO {
    private Integer recordId;
    private String diagnosis;
    private LocalDateTime createdAt;
    private String doctorFullName;
}
