package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.TechnicianDTO;
import com.group4.clinicmanagement.entity.Staff;
import com.group4.clinicmanagement.enums.UserStatus;
import com.group4.clinicmanagement.repository.TechnicianRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class TechnicianService {

    private final TechnicianRepository technicianRepository;
    private final PasswordEncoder passwordEncoder;

    public TechnicianService(TechnicianRepository technicianRepository,
                             PasswordEncoder passwordEncoder) {
        this.technicianRepository = technicianRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Staff findByUserId(Integer userId) {
        return technicianRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Technician not found with ID: " + userId));
    }

    @Transactional
    public void save(Staff user) {
        technicianRepository.save(user);
    }

    @Transactional
    public void updateProfile(Integer userId, TechnicianDTO dto, MultipartFile avatarFile) {
        Staff user = findByUserId(userId);

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender());

        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                Path uploadDir = Paths.get("uploads/avatars");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                String fileName = UUID.randomUUID() + "_" + avatarFile.getOriginalFilename();
                Path filePath = uploadDir.resolve(fileName);

                Files.copy(avatarFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                    Path oldFile = uploadDir.resolve(user.getAvatar());
                    if (Files.exists(oldFile)) {
                        Files.delete(oldFile);
                    }
                }

                user.setAvatar(fileName);

            } catch (IOException e) {
                throw new RuntimeException("Failed to upload avatar: " + e.getMessage(), e);
            }
        }

        technicianRepository.save(user);
    }

    @Transactional
    public boolean changePassword(Integer userId, String currentPassword, String newPassword, String confirmPassword) {
        Staff user = findByUserId(userId);

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            return false;
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        technicianRepository.save(user);
        return true;
    }

}
