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
    private Integer feedbackId;

    @NotNull(message = "Appointment is required")
    private Integer appointmentId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Minimum rating is 1 star")
    @Max(value = 5, message = "Maximum rating is 5 stars")
    private Float rating;

    @NotBlank(message = "Comment cannot be empty")
    private String comment;
}
