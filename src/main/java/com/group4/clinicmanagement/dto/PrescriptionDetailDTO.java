package com.group4.clinicmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PrescriptionDetailDTO {
    //DrugCatalog
    private String name;

    //PrescriptionDetails
    private Integer quantity;
    private String dosage;
    private String frequency;
    private Integer duration_days;
    private String instruction;
}