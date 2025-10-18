package com.group4.clinicmanagement.dto;

import com.group4.clinicmanagement.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@NoArgsConstructor
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
