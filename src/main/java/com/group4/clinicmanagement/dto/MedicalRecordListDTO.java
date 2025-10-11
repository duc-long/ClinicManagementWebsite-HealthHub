package com.group4.clinicmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
@AllArgsConstructor
@Data
public class MedicalRecordListDTO {
    private String diagnosis;
    private LocalDateTime createdAt;
    private String doctorFullName;
}
