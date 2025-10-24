package com.group4.clinicmanagement.service.admin;

import com.group4.clinicmanagement.dto.admin.PatientDTO;
import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.entity.Role;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.enums.UserStatus;
import com.group4.clinicmanagement.repository.PatientRepository;
import com.group4.clinicmanagement.repository.UserRepository;
import com.group4.clinicmanagement.repository.admin.PatientForAdminRepository;
import com.group4.clinicmanagement.util.PasswordUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
public class PatientForAdminService {
    private final PatientForAdminRepository patientForAdminRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordUtil  passwordUtil;


    public PatientForAdminService(PatientForAdminRepository patientForAdminRepository, PatientRepository patientRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, PasswordUtil passwordUtil) {
        this.patientForAdminRepository = patientForAdminRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordUtil = passwordUtil;
    }

    private PatientDTO mapToPatientDTO(Patient patient) {
        User user = patient.getUser();

        return new PatientDTO(user.getUserId(), patient.getPatientId(), user.getUsername(), user.getFullName(), user.getEmail(), user.getPhone(), user.getGender(), patient.getAddress(), patient.getUser().getAvatar(), patient.getDateOfBirth(), user.getStatus(), user.getRole().getRoleId());
    }

    @Transactional
    public Page<PatientDTO> findAll(Pageable pageable) {
        try {
            Page<Patient> patientPage = patientForAdminRepository.findAll(pageable);

            List<PatientDTO> dtoList = new ArrayList<>();
            for (Patient patient : patientPage.getContent()) {
                dtoList.add(mapToPatientDTO(patient));
            }

            return new PageImpl<>(dtoList, pageable, patientPage.getTotalElements());

        } catch (Exception e) {
            System.err.println("Error while fetching patients: " + e.getMessage());

            return Page.empty(pageable);
        }
    }

    @Transactional
    public PatientDTO findById(Integer id) {
        Patient patient = patientForAdminRepository.getPatientsByPatientId(id).orElseThrow(() -> new RuntimeException("Patient not found"));;
        return mapToPatientDTO(patient);
    }


    @Transactional
    public void update(PatientDTO dto, MultipartFile avatar) {
        Patient patient = patientForAdminRepository.findById(dto.getPatientId()).orElseThrow(() -> new RuntimeException("Patient not found"));
        User user = patient.getUser();

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender());
        user.setStatus(dto.getUserStatus());

        patient.setAddress(dto.getAddress());
        patient.setDateOfBirth(dto.getBirthDate());

        patient.setUser(user);
        patientRepository.save(patient);

        if (avatar != null && !avatar.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/avatars";
                Files.createDirectories(Paths.get(uploadDir));

                Optional<User> userOpt = userRepository.findUserByUsername(user.getUsername());
                String oldFilename = userOpt.map(User::getAvatar).orElse(null);

                String filename = UUID.randomUUID() + "_" + avatar.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, filename);

                avatar.transferTo(filePath.toFile());

                if (oldFilename != null && !oldFilename.isBlank()) {
                    Path oldFilePath = Paths.get(uploadDir, oldFilename);
                    Files.deleteIfExists(oldFilePath);
                }

                userRepository.updateAvatarFilename(user.getUsername(), filename);

            } catch (IOException e) {
                throw new RuntimeException("Upload avatar failed", e);
            }
        }
    }

    @Transactional
    public void deletePatient(PatientDTO dto) {
        Patient patient = patientForAdminRepository.findById(dto.getPatientId()).orElseThrow(() -> new RuntimeException("Patient not found"));
        patientRepository.delete(patient);
    }

    @Transactional
    public void newPatient(PatientDTO dto, MultipartFile avatar) {
        Integer getMaxId = userRepository.getMaxUserId().orElse(0);
        Patient patient = new Patient();
        User user = new User();
        if (getMaxId == 0) {
            user.setUserId(getMaxId);
            patient.setPatientId(user.getUserId());
        } else {
            user.setUserId(getMaxId + 1);
            patient.setPatientId(user.getUserId() + 1);
        }
        String encodePassword = passwordEncoder.encode(PasswordUtil.PASSWORD);

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender());
        user.setStatus(dto.getUserStatus());
        user.setUsername(dto.getUsername());
        user.setPasswordHash(encodePassword);
        user.setStatus(UserStatus.ACTIVE);

        Role role = new Role();
        role.setRoleId(2);
        user.setRole(role);

        patient.setAddress(dto.getAddress());
        patient.setDateOfBirth(dto.getBirthDate());

        patient.setUser(user);
        patientRepository.save(patient);


        if (avatar != null && !avatar.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/avatars";
                Files.createDirectories(Paths.get(uploadDir));

                Optional<User> userOpt = userRepository.findUserByUsername(user.getUsername());
                String oldFilename = userOpt.map(User::getAvatar).orElse(null);

                String filename = UUID.randomUUID() + "_" + avatar.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, filename);

                avatar.transferTo(filePath.toFile());

                if (oldFilename != null && !oldFilename.isBlank()) {
                    Path oldFilePath = Paths.get(uploadDir, oldFilename);
                    Files.deleteIfExists(oldFilePath);
                }

                userRepository.updateAvatarFilename(user.getUsername(), filename);

            } catch (IOException e) {
                throw new RuntimeException("Upload avatar failed", e);
            }
        }
    }


}
