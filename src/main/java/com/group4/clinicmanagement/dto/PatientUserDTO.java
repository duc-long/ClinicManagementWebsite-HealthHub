package com.group4.clinicmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class PatientUserDTO {
    private Integer userId;
    private Integer patientId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private Integer genderValue;
    private String address;
    private String avatarFilename;
}
