package com.group4.clinicmanagement.dto.registerpatient;

import com.group4.clinicmanagement.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.Period;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientRegisterDTO {
    @NotBlank(message = "Username must not be blank")
    @Size(min = 8, max = 50, message = "Username must be between 8 and 50 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_]{8,50}$",
            message = "Username can only contain letters, numbers, and underscores (no special characters)"
    )
    private String username;

    @NotBlank(message = "Full name must not be blank")
    private String fullName;

    @Email(message = "Invalid email address")
    @NotBlank(message = "Email must not be blank")
    private String email;

    @Pattern(regexp = "^[0-9]{9,11}$", message = "Phone number must contain 9–11 digits")
    @NotBlank(message = "Phone must not be blank")
    private String phone;

    @NotNull(message = "Gender must not be null")
    private Gender gender;

    @NotBlank(message = "Address must not be blank")
    private String address;

    @Past(message = "Date of birth must be in the past")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @AssertTrue(message = "Age must be between 10 and 120 years")
    public boolean isValidAge() {
        if (dateOfBirth == null) return true;
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        return age >= 10 && age <= 120;
    }
}
