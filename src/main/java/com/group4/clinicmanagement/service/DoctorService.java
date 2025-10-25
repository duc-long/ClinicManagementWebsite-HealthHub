package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.Department;
import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.repository.DepartmentRepository;
import com.group4.clinicmanagement.repository.DoctorRepository;
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
public class DoctorService {

    private DoctorRepository doctorRepository;
    private DepartmentRepository departmentRepository;
    private UserRepository userRepository;


    public DoctorService(DoctorRepository doctorRepository, DepartmentRepository departmentRepository,
                         UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    public List<Doctor> findAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<String> findAllDistinctSpecialties() {
        return doctorRepository.findAllDistinctSpecialties();
    }

    public List<Doctor> findDoctorByNameAndSpecialty(String name, String specialty) {
        return doctorRepository.findDoctorByNameAndSpecialty(name, specialty);

    }

    public Doctor findDoctorById(int id) {
        return doctorRepository.getDoctorByDoctorId(id);
    }

    public List<Doctor> getDoctorBySpecialtyIgnoreCase(String specialty) {
        return doctorRepository.getDoctorBySpecialtyIgnoreCase(specialty);
    }


    public Doctor findByDoctorId(Integer doctorId) {
        return doctorRepository.findById(doctorId).get();
    }

    public Department findDoctorDepartment(int departmentId) {
        Department department = departmentRepository.findByDepartmentId(departmentId)
                .orElse(null);

        return department;
    }

    @Transactional
    public void saveDoctor(Doctor doctor) {
        doctorRepository.save(doctor);
    }

    @Transactional
    public void savePatientUserWithAvatar(MultipartFile avatar, String username) {

        if (avatar != null && !avatar.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/uploads/avatars/doctor";
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
