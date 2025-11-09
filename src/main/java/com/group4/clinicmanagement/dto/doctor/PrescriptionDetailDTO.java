package com.group4.clinicmanagement.dto.doctor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PrescriptionDetailDTO {

    private int prescriptionDetailId;
    private int prescriptionId;
    @NotNull
    private int drugId;

    @NotBlank
    private String drugName;
    @NotBlank
    private String dosage;

    @NotNull
    @Min(1) @Max(50)
    private int quantity;

    @NotNull
    private int duration;

    @NotNull
    private String instruction;

    @NotNull
    private String frequency;
}