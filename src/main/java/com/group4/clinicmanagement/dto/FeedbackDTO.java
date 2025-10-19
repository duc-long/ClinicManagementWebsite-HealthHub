package com.group4.clinicmanagement.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FeedbackDTO {
    @NotNull
    @Min(1)
    @Max(5)
    private Float rating;


    @NotBlank
    @Size(max = 2000)
    private String comment;


    // Optionally link to appointment or patient
    private Integer appointmentId;
    private Integer patientId;
}
