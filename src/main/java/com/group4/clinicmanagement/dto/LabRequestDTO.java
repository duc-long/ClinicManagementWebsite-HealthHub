package com.group4.clinicmanagement.dto;

import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.entity.LabTestCatalog;
import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.enums.LabRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class LabRequestDTO {

    private Integer labRequestId;
    private Integer medicalRecordId;
    private Doctor doctor;
    private LabTestCatalog labTestCatalog;
    private Integer statusValue;
    private LabRequestStatus status;
    private LocalDateTime requestedAt;
    private Patient patient;

    public LabRequestDTO() {
    }
}
