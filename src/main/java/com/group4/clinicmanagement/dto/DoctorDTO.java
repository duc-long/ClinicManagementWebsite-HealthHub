package com.group4.clinicmanagement.dto;

import com.group4.clinicmanagement.enums.Gender;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class DoctorDTO {
    private Integer doctorId;
    private Integer patientId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private Gender gender;
    private String address;
    private String avatarFileName;
    private String bio;
    private Integer departmentId;
}
