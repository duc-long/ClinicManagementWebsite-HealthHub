package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.ReceptionistUserDTO;
import com.group4.clinicmanagement.dto.UserDTO;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.enums.Gender;
import com.group4.clinicmanagement.repository.ReceptionistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ReceptionistService {
    private final ReceptionistRepository receptionistRepository;

    public ReceptionistService(ReceptionistRepository receptionistRepository) {
        this.receptionistRepository = receptionistRepository;
    }

    private ReceptionistUserDTO convertToDTO(User user) {
        if (user == null) return null;
        return new ReceptionistUserDTO(
               user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getGender(),
                user.getAvatar(),
                user.getStatus()
        );
    }

    private void applyDTOToEntity(ReceptionistUserDTO dto, User user) {
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender());
    }

    @Transactional(readOnly = true)
    public ReceptionistUserDTO getReceptionistProfile(String receptionistName) {
        User user = receptionistRepository.findByUsername(receptionistName);
        System.out.println("\n====="+ user.getUsername());
        return convertToDTO(user);
    }

    @Transactional
    public void updateReceptionistProfile(String receptionistName, ReceptionistUserDTO dto, MultipartFile avatarFile) {
        User user = receptionistRepository.findByUsername(receptionistName);

        applyDTOToEntity(dto, user);
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String contentType = avatarFile.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new IllegalArgumentException("Invalid file type. Only image files are allowed.");
                }
                String originalFilename = avatarFile.getOriginalFilename().toLowerCase();
                if (!originalFilename.endsWith(".jpg") &&
                        !originalFilename.endsWith(".jpeg") &&
                        !originalFilename.endsWith(".png") &&
                        !originalFilename.endsWith(".gif") &&
                        !originalFilename.endsWith(".webp")) {
                    throw new IllegalArgumentException("Unsupported image format. Please upload JPG, PNG, GIF, or WEBP files.");
                }

                Path uploadDir = Paths.get("uploads/avatars");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                String fileName = "receptionist_" + UUID.randomUUID() + "_" + avatarFile.getOriginalFilename();
                Path filePath = uploadDir.resolve(fileName);
                Files.copy(avatarFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                    Path oldFile = uploadDir.resolve(user.getAvatar());
                    if (Files.exists(oldFile)) {
                        Files.delete(oldFile);
                    }
                }

                user.setAvatar(fileName);

            } catch (IllegalArgumentException e) {
                throw e;
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload avatar: " + e.getMessage(), e);
            }
        }

        receptionistRepository.save(user);
    }

}