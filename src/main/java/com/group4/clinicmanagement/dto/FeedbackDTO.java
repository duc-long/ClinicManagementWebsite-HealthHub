package com.group4.clinicmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FeedbackDTO {
    private Integer feedbackId;
    private Float rating;
    private String comment;
    private LocalDateTime feedbackDate;
    private String diagnosis;
    private LocalDateTime recordCreatedAt;
    private String patientName;
    private String username;
    private String avatar;
}
