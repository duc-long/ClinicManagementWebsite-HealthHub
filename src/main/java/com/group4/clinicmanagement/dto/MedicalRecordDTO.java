package com.group4.clinicmanagement.dto;

import com.group4.clinicmanagement.enums.RecordStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordDTO {
    private int recordId;
    private String diagnosis;
    private LocalDateTime createdAt;
    private String doctorName;
    private String notes;
    private RecordStatus recordStatus;
    private int doctorId;
    private int patientId;
    private int appointmentId;
    private String patientName;
}
