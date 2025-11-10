package com.group4.clinicmanagement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class VitalSignsDTO {
    private int vitalId;

    @NotNull(message = "Record ID is required")
    private Integer recordId;

    @NotNull(message = "Systolic blood pressure is required")
    @Positive(message = "Systolic must be greater than 0")
    private Integer systolic;

    @NotNull(message = "Diastolic blood pressure is required")
    @Positive(message = "Diastolic must be greater than 0")
    private Integer diastolic;

    @NotNull(message = "Height is required")
    @Positive(message = "Height must be greater than 0")
    private Double heightCm;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be greater than 0")
    private Double weightKg;

    // This value is auto-generated, no validation needed
    private String bloodPressure;

    @NotNull(message = "Heart rate is required")
    @Positive(message = "Heart rate must be greater than 0")
    private Integer heartRate;

    @NotNull(message = "Temperature is required")
    @Positive(message = "Temperature must be greater than 0")
    private Double temperature;

    private LocalDateTime recordedAt;

    public void updateBloodPressure() {
        this.bloodPressure = systolic + "/" + diastolic;
    }
}
