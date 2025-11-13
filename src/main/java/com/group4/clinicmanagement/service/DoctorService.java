package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.DoctorHomeDTO;
import com.group4.clinicmanagement.entity.Department;
import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.entity.Staff;
import com.group4.clinicmanagement.repository.DepartmentRepository;
import com.group4.clinicmanagement.repository.DoctorRepository;
import com.group4.clinicmanagement.repository.StaffRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DoctorService {

    private DoctorRepository doctorRepository;
    private DepartmentRepository departmentRepository;
    private StaffRepository staffRepository;


    public DoctorService(DoctorRepository doctorRepository, DepartmentRepository departmentRepository,
                         StaffRepository staffRepository) {
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
        this.staffRepository = staffRepository;
    }

    public List<Doctor> findAllDoctors() {
        return doctorRepository.findAll();
    }

    @Transactional
    public DoctorHomeDTO toDTO(Doctor doctor) {
        Staff user = doctor.getStaff();
        Department dept = doctor.getDepartment();

        return DoctorHomeDTO.builder()
                .doctorId(doctor.getDoctorId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .gender(user.getGender())
                .licenseNo(doctor.getLicenseNo())
                .specialty(doctor.getSpecialty())
                .degree(doctor.getDegree())
                .yearsExperience(doctor.getYearsExperience())
                .bio(doctor.getBio())
                .profileVisibility(Boolean.TRUE.equals(doctor.getProfileVisibility()))
                .departmentName(dept != null ? dept.getName() : null)
                .avatarFilename(user.getAvatar())
                .build();
    }

    @Transactional
    public List<DoctorHomeDTO> findAllVisibleAndActiveDoctors() {
        List<Doctor> doctors = doctorRepository.findAllVisibleAndActiveDoctors();
        List<DoctorHomeDTO> result = new ArrayList<>();

        for (Doctor doctor : doctors) {
            DoctorHomeDTO dto = toDTO(doctor);
            result.add(dto);
        }
        return result;
    }

    @Transactional
    public List<DoctorHomeDTO> findByNameAndDepartmentId(String name, Integer departmentId) {
        List<Doctor> doctors = doctorRepository.findByNameAndDepartmentId(name, departmentId);
        List<DoctorHomeDTO> result = new ArrayList<>();

        for (Doctor doctor : doctors) {
            DoctorHomeDTO dto = toDTO(doctor);
            result.add(dto);
        }
        return result;
    }

    @Transactional
    public DoctorHomeDTO findVisibleActiveDoctorById(Integer id) {
        Doctor doctor = doctorRepository.findVisibleActiveDoctorById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
        return toDTO(doctor);
    }

    @Transactional
    public List<DoctorHomeDTO> findVisibleActiveDoctorsByDepartment(String departmentName) {
        List<Doctor> doctors = doctorRepository.findVisibleActiveDoctorsByDepartment(departmentName);
        List<DoctorHomeDTO> result = new ArrayList<>();
        for (Doctor doctor : doctors) {
            DoctorHomeDTO dto = toDTO(doctor);
            result.add(dto);
        }
        return result;
    }

    @Transactional
    public List<DoctorHomeDTO> findTopDoctors(Pageable pageable) {
        List<Doctor> topDoctors = doctorRepository.findTopDoctors(pageable);
        List<DoctorHomeDTO> result = new ArrayList<>();
        for (Doctor doctor : topDoctors) {
            DoctorHomeDTO dto = toDTO(doctor);
            result.add(dto);
        }
        return result;
    }

    public Doctor findDoctorById(int id) {
        return doctorRepository.getDoctorByDoctorId(id);
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

                Optional<Staff> userOpt = staffRepository.findStaffByUsername(username);
                String oldFilename = userOpt.map(Staff::getAvatar).orElse(null);

                String filename = UUID.randomUUID() + "_" + avatar.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, filename);

                avatar.transferTo(filePath.toFile());

                if (oldFilename != null && !oldFilename.isBlank()) {
                    Path oldFilePath = Paths.get(uploadDir, oldFilename);
                    Files.deleteIfExists(oldFilePath);
                }

                staffRepository.updateAvatarFilename(username, filename);

            } catch (IOException e) {
                throw new RuntimeException("Upload avatar failed", e);
            }
        }
    }
}
