package com.group4.clinicmanagement.dto.doctor;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DrugCatalogDTO {
    private int drugId;
    private String drugName;
    private String description;
}
