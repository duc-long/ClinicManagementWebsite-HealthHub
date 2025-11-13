package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.PatientUserDTO;
import com.group4.clinicmanagement.dto.admin.PatientDTO;
import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.repository.PatientRepository;
import com.group4.clinicmanagement.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public PatientService(PatientRepository patientRepository, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.patientRepository = patientRepository;
    }

    public Optional<PatientUserDTO> getPatientsByUsername(String username) {
        return patientRepository.fetchPatientWithUserInfoByUsername(username);
    }

    public Patient findPatientById(int id) {
        return patientRepository.findById(id).orElse(null);
    }

    public PatientDTO findPatientDTOById(int id) {
        Patient patient = patientRepository.findById(id).orElse(null);
        return this.toDTO(patient);
    }

    @Transactional
    public PatientUserDTO savePatientUser(String username, PatientUserDTO dto) {
        // 1. Tìm patient theo username
        Patient patient = patientRepository.findPatientByUsername(username)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // 2. Update tất cả thông tin vào entity Patient
        patient.setFullName(dto.getFullName());
        patient.setEmail(dto.getEmail());
        patient.setPhone(dto.getPhone());
        patient.setGender(dto.getGender());
        patient.setAddress(dto.getAddress());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setUpdatedAt(java.time.LocalDateTime.now());

        // 3. Lưu
        patientRepository.save(patient);

        // 4. Trả về DTO đã cập nhật
        return patientRepository.fetchPatientWithUserInfoByUsername(username)
                .orElseThrow(() -> new RuntimeException("Updated profile not found"));
    }

    @Transactional
    public void savePatientUserWithAvatar(String username, PatientUserDTO dto, MultipartFile avatar) {
        // Bước 1: Lưu thông tin cơ bản (không liên quan ảnh)
        savePatientUser(username, dto);

        // Bước 2: Xử lý avatar nếu có upload
        if (avatar != null && !avatar.isEmpty()) {
            long maxFileSize = 20 * 1024 * 1024;
            if (avatar.getSize() > maxFileSize) {
                throw new IllegalArgumentException("File size exceeds the maximum allowed size of 20MB");
            }

            String fileExtension = getFileExtension(avatar.getOriginalFilename());
            if (!isValidFileExtension(fileExtension)) {
                throw new IllegalArgumentException("Invalid file type. Only JPG, PNG, and GIF are allowed.");
            }

            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/avatars";
                Files.createDirectories(Paths.get(uploadDir));

                // Lấy thông tin người dùng hiện tại
                Patient patient = patientRepository.findPatientByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Patient not found"));

                String oldFilename = patient.getAvatar();
                String newFilename = UUID.randomUUID() + "_" + avatar.getOriginalFilename();
                Path newFilePath = Paths.get(uploadDir, newFilename);

                // Lưu file mới
                avatar.transferTo(newFilePath.toFile());

                // Xóa ảnh cũ nếu có
                if (oldFilename != null && !oldFilename.isBlank()) {
                    Path oldFilePath = Paths.get(uploadDir, oldFilename);
                    Files.deleteIfExists(oldFilePath);
                }

                // Cập nhật avatar mới vào DB
                patientRepository.updateAvatarFilename(username, newFilename);

            } catch (IOException e) {
                throw new RuntimeException("Upload avatar failed", e);
            }
        }
    }

    // Kiểm tra đuôi file có hợp lệ không (chỉ chấp nhận jpg, png, gif)
    private boolean isValidFileExtension(String extension) {
        String[] allowedExtensions = {"jpg", "jpeg", "png", "gif"};
        for (String ext : allowedExtensions) {
            if (ext.equalsIgnoreCase(extension)) {
                return true;  // Tệp hợp lệ
            }
        }
        return false;  // Tệp không hợp lệ
    }

    // Lấy đuôi file từ tên file
    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        }
        return "";
    }

    public boolean changePassword(String username, String currentPassword, String newPassword) {
        Optional<Patient> optionalUser = patientRepository.findByUsername(username);

        if (optionalUser.isEmpty()) return false;

        Patient user = optionalUser.get();

        // So sánh mật khẩu hiện tại
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            return false;
        }

        // Cập nhật mật khẩu mới đã băm
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        patientRepository.save(user);
        return true;
    }


    public PatientDTO toDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setPatientId(patient.getPatientId());
        dto.setUsername(patient.getUsername());
        dto.setFullName(patient.getFullName());
        dto.setEmail(patient.getEmail());
        dto.setPhone(patient.getPhone());
        dto.setGender(patient.getGender());
        dto.setAddress(patient.getAddress());
        dto.setAvatarFilename(patient.getAvatar());
        dto.setBirthDate(patient.getDateOfBirth());
        dto.setStatus(patient.getStatus());
        return dto;
    }

    @Transactional
    public Page<PatientDTO> findAll(Pageable pageable) {
        try {
            Page<Patient> patientPage = patientRepository.findAll(pageable);

            List<PatientDTO> dtoList = new ArrayList<>();
            for (Patient patient : patientPage.getContent()) {
                dtoList.add(toDTO(patient));
            }

            return new PageImpl<>(dtoList, pageable, patientPage.getTotalElements());

        } catch (Exception e) {
            System.err.println("Error while fetching patients: " + e.getMessage());

            return Page.empty(pageable);
        }
    }

    public PatientDTO findById(Integer id) {
        Optional<Patient> patientOpt = patientRepository.findById(id);
        return patientOpt.map(this::toDTO).orElse(null);
    }


}
