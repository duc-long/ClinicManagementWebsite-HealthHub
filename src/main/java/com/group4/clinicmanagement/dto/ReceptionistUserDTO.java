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
public class ReceptionistUserDTO {
    private Integer userId;
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

    private String avatarFilename;
    private UserStatus userStatus;
}
