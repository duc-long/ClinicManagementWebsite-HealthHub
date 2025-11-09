package com.group4.clinicmanagement.dto;

import com.group4.clinicmanagement.entity.*;
import com.group4.clinicmanagement.enums.LabRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    public LabRequestDTO(LabRequest labRequest) {
        this.labRequestId = labRequest.getLabRequestId();
        this.doctor = labRequest.getDoctor();
        this.medicalRecordId = labRequest.getMedicalRecord().getRecordId();
        this.labTestCatalog = labRequest.getTest();
        this.requestedAt = labRequest.getRequestedAt();
        this.statusValue = labRequest.getStatusValue();
        this.status = labRequest.getStatus();
        this.requestedAt = labRequest.getRequestedAt();
        this.patient = labRequest.getMedicalRecord().getPatient();
    }
}
