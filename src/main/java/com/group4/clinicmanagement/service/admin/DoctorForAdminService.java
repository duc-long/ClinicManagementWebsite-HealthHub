package com.group4.clinicmanagement.service.admin;

import com.group4.clinicmanagement.dto.admin.DoctorDTO;
import com.group4.clinicmanagement.entity.Department;
import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.entity.Role;
import com.group4.clinicmanagement.entity.Staff;
import com.group4.clinicmanagement.enums.UserStatus;
import com.group4.clinicmanagement.repository.DoctorRepository;
import com.group4.clinicmanagement.repository.StaffRepository;
import com.group4.clinicmanagement.util.PasswordUtil;
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
public class DoctorForAdminService {

    private final DoctorRepository doctorRepository;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordUtil passwordUtil;

    public DoctorForAdminService(DoctorRepository doctorRepository, StaffRepository staffRepository, PasswordEncoder passwordEncoder, PasswordUtil passwordUtil) {
        this.doctorRepository = doctorRepository;
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordUtil = passwordUtil;
    }


    private DoctorDTO mapToDoctorDTO(Doctor doctor) {
        Staff user = doctor.getStaff();

        return new DoctorDTO(
                user.getStaffId(),
                doctor.getDoctorId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getGender(),
                user.getRole().getRoleId(),
                user.getAvatar(),
                doctor.getLicenseNo(),
                doctor.getSpecialty(),
                doctor.getDegree(),
                doctor.getYearsExperience(),
                doctor.getBio(),
                user.getStatus(),
                doctor.getDepartment());
    }

    @Transactional
    public Page<DoctorDTO> findAllDoctors(Pageable pageable) {
        try {
            Page<Doctor> doctorPage = doctorRepository.findAll(pageable);

            List<DoctorDTO> dtoList = new ArrayList<>();
            for (Doctor doctor : doctorPage.getContent()) {
                dtoList.add(mapToDoctorDTO(doctor));
            }

            return new PageImpl<>(dtoList, pageable, doctorPage.getTotalElements());

        } catch (Exception e) {
            System.err.println("Error while fetching doctors: " + e.getMessage());

            return Page.empty(pageable);
        }
    }

    @Transactional
    public DoctorDTO findById(Integer id) {
        Doctor doctor = doctorRepository.getDoctorsByDoctorId(id).orElseThrow(() -> new RuntimeException("Doctor not found"));
        ;
        return mapToDoctorDTO(doctor);
    }

    @Transactional
    public void update(DoctorDTO dto, MultipartFile avatar) {
        Doctor doctor = doctorRepository.findById(dto.getDoctorId()).orElseThrow(() -> new RuntimeException("Doctor not found"));
        Staff user = doctor.getStaff();

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender());
        user.setStatus(dto.getUserStatus());

        doctor.setLicenseNo(dto.getLicenseNo());
        doctor.setSpecialty(dto.getSpecialty());
        doctor.setDegree(dto.getDegree());
        doctor.setYearsExperience(dto.getYearsExperience());
        doctor.setBio(dto.getBio());

        Department depRef = new Department();
        depRef.setDepartmentId(dto.getDepartment().getDepartmentId());
        doctor.setDepartment(depRef);

        doctor.setStaff(user);
        doctorRepository.save(doctor);
        if (avatar != null && !avatar.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/avatars";
                Files.createDirectories(Paths.get(uploadDir));

                Optional<Staff> userOpt = staffRepository.findStaffByUsername(user.getUsername());
                String oldFilename = userOpt.map(Staff::getAvatar).orElse(null);

                String filename = UUID.randomUUID() + "_" + avatar.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, filename);

                avatar.transferTo(filePath.toFile());

                if (oldFilename != null && !oldFilename.isBlank()) {
                    Path oldFilePath = Paths.get(uploadDir, oldFilename);
                    Files.deleteIfExists(oldFilePath);
                }

                staffRepository.updateAvatarFilename(user.getUsername(), filename);

            } catch (IOException e) {
                throw new RuntimeException("Upload avatar failed", e);
            }
        }
    }

    public boolean isLicenseNoDuplicateForUpdate(String LicenseNo, Integer id) {
        Doctor doctor = doctorRepository.getDoctorsByDoctorId(id).orElseThrow(() -> new RuntimeException("Doctor not found"));
        ;
        if (doctor.getLicenseNo().equals(LicenseNo)) {
            return false;
        } else {
            return doctorRepository.findDoctorsByLicenseNo(LicenseNo).isPresent();
        }
    }

    public boolean isLicenseNoDuplicateForNewDoctor(String LicenseNo, Integer id) {
        return doctorRepository.findDoctorsByLicenseNo(LicenseNo).isPresent();
    }

    public Integer newDoctor(@Valid DoctorDTO dto, MultipartFile avatar) {
        Doctor doctor = new Doctor();
        Staff user = new Staff();
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
        role.setRoleId(1);
        user.setRole(role);

        doctor.setLicenseNo(dto.getLicenseNo());
        doctor.setSpecialty(dto.getSpecialty());
        doctor.setDegree(dto.getDegree());
        doctor.setYearsExperience(dto.getYearsExperience());
        doctor.setBio(dto.getBio());

        Department depRef = new Department();
        depRef.setDepartmentId(dto.getDepartment().getDepartmentId());
        doctor.setDepartment(depRef);

        doctor.setStaff(user);
        doctorRepository.save(doctor);

        if (avatar != null && !avatar.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/avatars";
                Files.createDirectories(Paths.get(uploadDir));

                Optional<Staff> userOpt = staffRepository.findStaffByUsername(user.getUsername());
                String oldFilename = userOpt.map(Staff::getAvatar).orElse(null);

                String filename = UUID.randomUUID() + "_" + avatar.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, filename);

                avatar.transferTo(filePath.toFile());

                if (oldFilename != null && !oldFilename.isBlank()) {
                    Path oldFilePath = Paths.get(uploadDir, oldFilename);
                    Files.deleteIfExists(oldFilePath);
                }

                staffRepository.updateAvatarFilename(user.getUsername(), filename);

            } catch (IOException e) {
                throw new RuntimeException("Upload avatar failed", e);
            }
        }

        return doctor.getDoctorId();
    }

    @Transactional
    public void deleteDoctor(DoctorDTO dto) {
        Doctor doctor = doctorRepository.findById(dto.getDoctorId()).orElseThrow(() -> new RuntimeException("Doctor not found"));
        doctorRepository.delete(doctor);
    }
}
