package com.group4.clinicmanagement.dto.doctor;

import com.group4.clinicmanagement.enums.LabRequestStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class LabRequestDTO {

    @Positive(message = "labRequestId must be a positive number")
    private Integer labRequestId;

    @NotNull(message = "medicalRecordId is required")
    @Positive(message = "medicalRecordId must be a positive number")
    private Integer medicalRecordId;

    @NotNull(message = "doctorId is required")
    @Positive(message = "doctorId must be a positive number")
    private Integer doctorId;

    @NotNull(message = "labTestCatalogId is required")
    @Positive(message = "labTestCatalogId must be a positive number")
    private Integer labTestCatalogId;

    private LabRequestStatus status;

    @Min(value = 0, message = "statusValue must be within valid range")
    @Max(value = 3, message = "statusValue must be within valid range")
    private Integer statusValue;
    private LocalDateTime requestedAt;
}