package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.CashierUserDTO;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.repository.CashierRepository;
import com.group4.clinicmanagement.repository.UserRepository;
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
public class CashierService {
    private final  CashierRepository cashierRepository;

    public CashierService(CashierRepository cashierRepository) {
        this.cashierRepository = cashierRepository;
    }

    private CashierUserDTO convertToDTO(User user) {
        if (user == null) return null;
        return new CashierUserDTO(
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

    private void applyDTOToEntity(CashierUserDTO dto, User user) {
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender());
    }

    @Transactional(readOnly = true)
    public CashierUserDTO getCashierProfile(String cashierName) {
        User user = cashierRepository.findByUsername(cashierName);
        System.out.println("\n====="+ user.getUsername());
        return convertToDTO(user);
    }

    @Transactional
    public void updateCashierProfile(String cashierName, CashierUserDTO dto, MultipartFile avatarFile) {
        User user = cashierRepository.findByUsername(cashierName);
        applyDTOToEntity(dto, user);

        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String contentType = avatarFile.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new IllegalArgumentException("Invalid file type. Only image files are allowed.");
                }

                String filenameLower = avatarFile.getOriginalFilename().toLowerCase();
                if (!(filenameLower.endsWith(".jpg") || filenameLower.endsWith(".jpeg")
                        || filenameLower.endsWith(".png") || filenameLower.endsWith(".gif")
                        || filenameLower.endsWith(".webp"))) {
                    throw new IllegalArgumentException("Unsupported image format. Please upload JPG, PNG, GIF, or WEBP files.");
                }

                if (avatarFile.getSize() > 5 * 1024 * 1024) {
                    throw new IllegalArgumentException("File is too large. Maximum allowed size is 5MB.");
                }

                Path uploadDir = Paths.get("uploads/avatars");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                String newFilename = "cashier_" + UUID.randomUUID() + "_" + avatarFile.getOriginalFilename();
                Path filePath = uploadDir.resolve(newFilename);
                Files.copy(avatarFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                    Path oldFile = uploadDir.resolve(user.getAvatar());
                    if (Files.exists(oldFile)) {
                        Files.delete(oldFile);
                    }
                }

                user.setAvatar(newFilename);

            } catch (IllegalArgumentException e) {
                throw e;
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload avatar: " + e.getMessage(), e);
            }
        }
        cashierRepository.save(user);
    }

}
