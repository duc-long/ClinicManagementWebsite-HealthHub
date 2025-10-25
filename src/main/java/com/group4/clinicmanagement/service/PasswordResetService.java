package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.email.PasswordResetTokenDTO;
import com.group4.clinicmanagement.entity.PasswordResetToken;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.repository.PasswordResetTokenRepository;
import com.group4.clinicmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    public  PasswordResetService(PasswordResetTokenRepository tokenRepository,
                                 UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public void sendResetLink(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        PasswordResetTokenDTO dto = new PasswordResetTokenDTO();
        dto.setToken(UUID.randomUUID().toString());
        dto.setEmail(email);
        dto.setOtpCode(String.format("%06d", new Random().nextInt(999999)));
        dto.setExpirationDate(LocalDateTime.now().plusMinutes(15));

        tokenRepository.deleteByEmail(email);
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(dto.getToken());
        token.setEmail(dto.getEmail());
        token.setOtpCode(dto.getOtpCode());
        token.setExpirationDate(dto.getExpirationDate());

        tokenRepository.save(token);

        String link = "http://localhost:8080/reset-password?token=" + dto.getToken();
        String subject = "HealthHub - Password Reset";
        String body = """
                Hello %s,
                
                You requested to reset your password.
                
                👉 Your OTP Code: %s
                (Valid for 15 minutes)
                
                Or click this link to reset directly:
                %s
                
                If you didn't request this, please ignore this email.
                
                Regards,
                HealthHub Support
                """.formatted(user.getFullName(), dto.getOtpCode(), link);

        emailService.sendEmail(email, subject, body);
    }

    @Transactional
    public boolean verifyOtp(String email, String otp) {
        var token = tokenRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (token.isExpired()) throw new RuntimeException("OTP expired");
        return token.getOtpCode().equals(otp);
    }

    @Transactional
    public boolean resetPasswordWithToken(String token, String newPassword) {
        var resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (resetToken.isExpired()) throw new RuntimeException("Token expired");

        var user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);
        return true;
    }

    @Transactional
    public boolean resetPasswordWithOtp(String email, String otp, String newPassword) {
        if (!verifyOtp(email, otp)) throw new RuntimeException("Invalid OTP");
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.deleteByEmail(email);
        return true;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
    }
}
