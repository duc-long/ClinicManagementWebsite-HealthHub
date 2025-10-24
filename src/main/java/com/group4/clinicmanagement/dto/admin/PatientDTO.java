package com.group4.clinicmanagement.dto.admin;

import com.group4.clinicmanagement.enums.Gender;
import com.group4.clinicmanagement.enums.UserStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDTO {

    private Integer userId;

    private Integer patientId;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits")
    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Address is required")
    private String address;

    private String avatarFilename;

    @Past(message = "Birth date must be in the past")
    @NotNull(message = "Birth date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private UserStatus userStatus;

    private Integer roleId;
}
