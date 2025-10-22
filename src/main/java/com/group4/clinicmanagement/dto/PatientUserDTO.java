package com.group4.clinicmanagement.dto;

import com.group4.clinicmanagement.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private Gender gender;
    private String address;
    private String avatarFilename;
    private LocalDate dateOfBirth;

    public PatientUserDTO(Integer userId, Integer patientId, String username, String fullName,
                          String email, String phone,
                          Integer genderValue, String address, String avatarFilename, LocalDate dateOfBirth) {
        this.userId = userId;
        this.patientId = patientId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.gender = Gender.fromInt(genderValue!=null ? genderValue : 0);
        this.address = address;
        this.avatarFilename = avatarFilename;
        this.dateOfBirth = dateOfBirth;
    }
}
