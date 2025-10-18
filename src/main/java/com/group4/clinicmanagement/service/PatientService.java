package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.PatientUserDTO;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.repository.PatientRepository;
import com.group4.clinicmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public PatientService(PatientRepository patientRepository, UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    public Optional<PatientUserDTO> getPatientsByUsername(String username) {
        return patientRepository.fetchPatientWithUserInfoByUsername(username);
    }

    @Transactional
    public PatientUserDTO savePatientUser(String username, PatientUserDTO patientUserDTO) {
        int updatedUser = userRepository.updateProfileByUsername(
                username,
                patientUserDTO.getFullName(),
                patientUserDTO.getEmail(),
                patientUserDTO.getPhone(),
                patientUserDTO.getGender().getValue()
        );

        if (updatedUser == 0) {
            throw new RuntimeException("User not found");
        }

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int updatedPatient = patientRepository.updateAddress(user.getUserId(), patientUserDTO.getAddress(), patientUserDTO.getDateOfBirth());

        if (updatedPatient == 0) {
            throw new RuntimeException("Patient not found");
        }

        return patientRepository.fetchPatientWithUserInfoByUsername(username)
                .orElseThrow(() -> new RuntimeException("Updated profile not found"));
    }

    @Transactional
    public void savePatientUserWithAvatar(String username, PatientUserDTO dto, MultipartFile avatar) {
        savePatientUser(username, dto);

        if (avatar != null && !avatar.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/avatars";
                Files.createDirectories(Paths.get(uploadDir));

                Optional<User> userOpt = userRepository.findUserByUsername(username);
                String oldFilename = userOpt.map(User::getAvatar).orElse(null);

                String filename = UUID.randomUUID() + "_" + avatar.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, filename);

                avatar.transferTo(filePath.toFile());

                if (oldFilename != null && !oldFilename.isBlank()) {
                    Path oldFilePath = Paths.get(uploadDir, oldFilename);
                    Files.deleteIfExists(oldFilePath);
                }

                userRepository.updateAvatarFilename(username, filename);

            } catch (IOException e) {
                throw new RuntimeException("Upload avatar failed", e);
            }
        }



    }
}
