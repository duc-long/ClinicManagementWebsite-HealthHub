package com.group4.clinicmanagement.dto.admin;

import com.group4.clinicmanagement.enums.Gender;
import com.group4.clinicmanagement.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@AllArgsConstructor
@Data
public class PatientDTO {
    private Integer userId;
    private Integer patientId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private Gender gender;
    private String address;
    private String avatarFilename;
    private LocalDate birthDate;
    private UserStatus userStatus;
    private Integer roleId;
}
