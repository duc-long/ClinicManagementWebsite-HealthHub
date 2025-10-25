package com.group4.clinicmanagement.dto.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetDTO {
    // Bước 1: yêu cầu gửi email
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    // Bước 2: xác minh OTP
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    private String otp;

    // Bước 3: xác minh token
    private String token;

    // Bước 4: đặt lại mật khẩu
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;

    private String confirmPassword;
}
