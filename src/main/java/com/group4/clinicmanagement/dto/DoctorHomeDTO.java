package com.group4.clinicmanagement.dto;

import com.group4.clinicmanagement.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorHomeDTO {
    private Integer doctorId;

    private String fullName;
    private String email;
    private String phone;
    private Gender gender;

    private String licenseNo;
    private String specialty;
    private String degree;
    private Integer yearsExperience;
    private String bio;
    private boolean profileVisibility;


    private String departmentName;
    private String avatarFilename;
}
