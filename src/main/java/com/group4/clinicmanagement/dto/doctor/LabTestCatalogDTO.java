package com.group4.clinicmanagement.dto.doctor;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabTestCatalogDTO {

    private Integer testId;

    @NotBlank(message = "Test name must not be empty")
    @Size(max = 200, message = "Test name must not exceed 200 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Cost must not be null")
    @PositiveOrZero(message = "Cost must be a non-negative value")
    private Double cost;

    @NotNull(message = "Status must not be null")
    @Min(value = 0, message = "Status must be either 0 or 1")
    @Max(value = 1, message = "Status must be either 0 or 1")
    private Integer status;

    private LocalDateTime createdAt;
}