package com.group4.clinicmanagement.service.admin;

import com.group4.clinicmanagement.dto.admin.PatientDTO;
import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.entity.Role;
import com.group4.clinicmanagement.entity.Staff;
import com.group4.clinicmanagement.enums.UserStatus;
import com.group4.clinicmanagement.repository.PatientRepository;
import com.group4.clinicmanagement.repository.StaffRepository;
import com.group4.clinicmanagement.util.PasswordUtil;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PatientForAdminService {
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordUtil passwordUtil;

    public PatientForAdminService(PatientRepository patientRepository, PasswordEncoder passwordEncoder, PasswordUtil passwordUtil) {
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordUtil = passwordUtil;
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

    @Transactional
    public PatientDTO findById(Integer id) {
        Patient patient = patientRepository.getPatientsByPatientId(id).orElseThrow(() -> new RuntimeException("Patient not found"));
        ;
        return toDTO(patient);
    }


    @Transactional
    public void update(PatientDTO dto, MultipartFile avatar) {
        Patient patient = patientRepository.findById(dto.getPatientId()).orElseThrow(() -> new RuntimeException("Patient not found"));


        patient.setFullName(dto.getFullName());
        patient.setEmail(dto.getEmail());
        patient.setPhone(dto.getPhone());
        patient.setGender(dto.getGender());
        patient.setStatus(dto.getStatus());
        patient.setAddress(dto.getAddress());
        patient.setDateOfBirth(dto.getBirthDate());

        patientRepository.save(patient);

        if (avatar != null && !avatar.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/avatars";
                Files.createDirectories(Paths.get(uploadDir));

                Optional<Patient> userOpt = patientRepository.findPatientByUsername(patient.getUsername());
                String oldFilename = userOpt.map(Patient::getAvatar).orElse(null);

                String filename = UUID.randomUUID() + "_" + avatar.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, filename);

                avatar.transferTo(filePath.toFile());

                if (oldFilename != null && !oldFilename.isBlank()) {
                    Path oldFilePath = Paths.get(uploadDir, oldFilename);
                    Files.deleteIfExists(oldFilePath);
                }

                patientRepository.updateAvatarFilename(patient.getUsername(), filename);

            } catch (IOException e) {
                throw new RuntimeException("Upload avatar failed", e);
            }
        }
    }

    @Transactional
    public void deletePatient(PatientDTO dto) {
        Patient patient = patientRepository.findById(dto.getPatientId()).orElseThrow(() -> new RuntimeException("Patient not found"));
        patientRepository.delete(patient);
    }

    @Transactional
    public Integer newPatient(PatientDTO dto, MultipartFile avatar) {
        Patient patient = new Patient();
        String encodePassword = passwordEncoder.encode(PasswordUtil.PASSWORD);

        patient.setFullName(dto.getFullName());
        patient.setEmail(dto.getEmail());
        patient.setPhone(dto.getPhone());
        patient.setGender(dto.getGender());
        patient.setStatus(dto.getStatus());
        patient.setUsername(dto.getUsername());
        patient.setPasswordHash(encodePassword);
        patient.setStatus(UserStatus.ACTIVE);
        patient.setAddress(dto.getAddress());
        patient.setDateOfBirth(dto.getBirthDate());
        patientRepository.save(patient);

        if (avatar != null && !avatar.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/avatars";
                Files.createDirectories(Paths.get(uploadDir));

                Optional<Patient> userOpt = patientRepository.findPatientByUsername(patient.getUsername());
                String oldFilename = userOpt.map(Patient::getAvatar).orElse(null);

                String filename = UUID.randomUUID() + "_" + avatar.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, filename);

                avatar.transferTo(filePath.toFile());

                if (oldFilename != null && !oldFilename.isBlank()) {
                    Path oldFilePath = Paths.get(uploadDir, oldFilename);
                    Files.deleteIfExists(oldFilePath);
                }

                patientRepository.updateAvatarFilename(patient.getUsername(), filename);

            } catch (IOException e) {
                throw new RuntimeException("Upload avatar failed", e);
            }
        }
        return patient.getPatientId();
    }
    public boolean isMailNoDuplicateForUpdPatient(String mail, Integer id) {
        Patient user = patientRepository.findPatientByPatientId(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getEmail().equals(mail)) {
            return false;
        } else {
            return patientRepository.findPatientByemail(mail).isPresent();
        }
    }

    public boolean isMailNoDuplicateForNewPatient(String mail) {
        return patientRepository.findPatientByemail(mail).isPresent();
    }


    public boolean isUsernameDuplicateForNewPatient(String username) {
        return patientRepository.findByUsername(username).isPresent();
    }

    public boolean isUsernameDuplicateForUpdPatient(String userName, Integer id) {
        Patient user = patientRepository.findPatientByPatientId(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getUsername().equals(userName)) {
            return false;
        } else {
            return patientRepository.findByUsername(userName).isPresent();
        }
    }

    public boolean isPhoneNoDuplicateForUpd(String phone, Integer id) {
        Patient user = patientRepository.findPatientByPatientId(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getPhone().equals(phone)) {
            return false;
        } else {
            return patientRepository.findPatientByPhone(phone).isPresent();
        }
    }

    public boolean isPhoneNoDuplicateForNew(String phone) {
        return patientRepository.findPatientByPhone(phone).isPresent();
    }

    public long getTotalPatientByFilter(String filter) {
        long totalPatients = 0;
        switch (filter.toLowerCase()) {
            case "today": {
                return totalPatients = patientRepository.countPatientsToday(LocalDateTime.now());
            }
            case "year": {
                return totalPatients = patientRepository.countPatientsThisYear(LocalDateTime.now().getYear());
            }
            default: {
                return totalPatients = patientRepository.countPatientsThisMonth(LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue());
            }
        }
    }
}
