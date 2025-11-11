package com.group4.clinicmanagement.dto;

import com.group4.clinicmanagement.enums.Gender;
import com.group4.clinicmanagement.enums.UserStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashierUserDTO {
    private Integer userId;

    private String username;

    @NotBlank(message = "Full name must not be blank")
    @Size(min = 5, max = 100, message = "Full name must be between 5 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email address")
    @Pattern(
            regexp = "^[a-zA-Z0-9]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Please enter a valid email address (e.g., user@example.com)."
    )
    private String email;

    @Pattern(
            regexp = "(0[3|5|7|8|9])+([0-9]{8})\\b",
            message = "Please enter a valid 10-digit phone number starting with 03, 05, 07, 08, or 09"
    )
    @NotBlank(message = "Phone must not be blank")
    private String phone;

    @NotNull(message = "Gender must not be null")
    private Gender gender;

    private String avatarFilename;

    private UserStatus userStatus;
}
