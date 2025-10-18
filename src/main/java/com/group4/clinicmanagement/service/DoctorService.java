package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.DoctorUserDTO;
import com.group4.clinicmanagement.entity.Department;
import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.repository.DepartmentRepository;
import com.group4.clinicmanagement.repository.DoctorRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository, DepartmentRepository departmentRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    public DoctorUserDTO toDTO(Doctor doctor) {
        User user = doctor.getUser();
        Department dept = doctor.getDepartment();

        return DoctorUserDTO.builder()
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
                .build();
    }

    @Transactional
    public List<DoctorUserDTO> findAllVisibleAndActiveDoctorsDoctorUserDTOS() {
        List<Doctor> doctors = doctorRepository.findAllVisibleAndActiveDoctors();
        List<DoctorUserDTO> result = new ArrayList<>();

        for (Doctor doctor : doctors) {
            DoctorUserDTO dto = toDTO(doctor);
            result.add(dto);
        }

        return result;
    }

    @Transactional
    public List<DoctorUserDTO> findByNameContainingIgnoreCaseAndDepartmentId(String name, Integer departmentId) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndDepartmentId(name, departmentId);
        List<DoctorUserDTO> result = new ArrayList<>();

        for (Doctor doctor : doctors) {
            DoctorUserDTO dto = toDTO(doctor);
            result.add(dto);
        }
        return result;
    }

    @Transactional
    public DoctorUserDTO findVisibleActiveDoctorById(Integer id) {
        Doctor doctor = doctorRepository.findVisibleActiveDoctorById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
        return toDTO(doctor);
    }

    @Transactional
    public List<DoctorUserDTO> findVisibleActiveDoctorsByDepartment(String departmentName) {
        List<Doctor> doctors = doctorRepository.findVisibleActiveDoctorsByDepartment(departmentName);
        List<DoctorUserDTO> result = new ArrayList<>();
        for (Doctor doctor : doctors) {
            DoctorUserDTO dto = toDTO(doctor);
            result.add(dto);
        }
        return result;
    }

    @Transactional
    public List<DoctorUserDTO> findTopDoctors(Pageable pageable) {
        List<Doctor> topDoctors = doctorRepository.findTopDoctors(pageable);
        List<DoctorUserDTO> result = new ArrayList<>();
        for (Doctor doctor : topDoctors) {
            DoctorUserDTO dto = toDTO(doctor);
            result.add(dto);
        }
        return result;
    }

}
