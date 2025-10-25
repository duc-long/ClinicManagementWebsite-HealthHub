package com.group4.clinicmanagement.dto;

import com.group4.clinicmanagement.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PatientUserDTO {
    private Integer userId;
    private Integer patientId;
    private String username;

    @NotBlank(message = "Full name must not be blank")
    @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email address")
    private String email;

    @Pattern(regexp = "^[0-9]{9,11}$", message = "Phone number must contain 9–11 digits")
    @NotBlank(message = "Phone must not be blank")
    private String phone;

    @NotNull(message = "Gender must not be null")
    private Gender gender;

    @NotBlank(message = "Address must not be blank")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    private String avatarFilename;

    @Past(message = "Date of birth must be in the past")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @AssertTrue(message = "Age must be between 10 and 120 years")
    public boolean isValidAge() {
        if (dateOfBirth == null) return true;
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        return age >= 10 && age <= 120;
    }

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
