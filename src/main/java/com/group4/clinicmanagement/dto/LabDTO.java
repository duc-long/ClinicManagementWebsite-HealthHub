package com.group4.clinicmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LabDTO {
    //LabTestCatalog
    private String nameLabTest;
    private String descriptionLabTest;
    //LabResult
    private String resultText;

    //LabImage
    private String filePath;
    private String description;
}