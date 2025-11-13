package com.group4.clinicmanagement.dto.admin;

import com.group4.clinicmanagement.entity.Department;
import com.group4.clinicmanagement.enums.Gender;
import com.group4.clinicmanagement.enums.UserStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDTO {

    private Integer userId;

    private Integer doctorId;

    @NotBlank(message = "Username is required")
    @Size(max = 100, message = "Username must be at most 100 characters")
    private String username;

    @NotBlank(message = "Full name is required")
    @Size(max = 200, message = "Full name must be at most 200 characters")
    private String fullName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String email;

    @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits")
    private String phone;

    @NotNull(message = "Gender is required")
    private Gender gender;

    private Integer roleId;

    private String avatarFilename;


    // Doctor-specific info
    @Pattern(
            regexp = "^(\\d{6}/BYT-CCHN|\\d{6}/SYT-[A-Z]{2,3}|GPHN-\\d{4}-\\d{5}/BYT)$",
            message = "License must match one of the Exs: 000000/BYT-CCHN, 000000/SYT-HCM"
    )
    @NotBlank(message = "License number is required")
    @Size(max = 50)
    private String licenseNo;

    @NotBlank(message = "Specialty is required")
    @Size(max = 100)
    private String specialty;

    @Size(max = 100)
    private String degree;

    @Min(value = 0, message = "Years of experience must be non-negative")
    private Integer yearsExperience;

    @Size(max = 1000)
    private String bio;

    private UserStatus userStatus;

    private Department department;
}
