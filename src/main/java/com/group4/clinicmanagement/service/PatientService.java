package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.PatientUserDTO;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.repository.PatientRepository;
import com.group4.clinicmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public PatientService(PatientRepository patientRepository, UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    public List<PatientUserDTO> getAllPatients() {
        return patientRepository.fetchPatientWithUserInfo();
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
                patientUserDTO.getGenderValue()
        );

        if (updatedUser == 0) {
            throw new RuntimeException("User not found");
        }

        // Lấy userId để cập nhật bảng Patient
        User user = (User) userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int updatedPatient = patientRepository.updateAddress(user.getUserId(), patientUserDTO.getAddress());

        if (updatedPatient == 0) {
            throw new RuntimeException("Patient not found");
        }

        // Trả về DTO đã cập nhật
        return patientRepository.fetchPatientWithUserInfoByUsername(username)
                .orElseThrow(() -> new RuntimeException("Updated profile not found"));
    }
}
