package com.group4.clinicmanagement.dto.registerpatient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetPasswordDTO {
    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 50)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$",
            message = "Password must contain upper, lower, number, and special character")
    private String password;

    @NotBlank
    private String confirmPassword;
}
