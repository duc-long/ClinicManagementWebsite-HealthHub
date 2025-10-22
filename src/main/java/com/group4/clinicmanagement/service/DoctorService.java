package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.Department;
import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.repository.DepartmentRepository;
import com.group4.clinicmanagement.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    private DoctorRepository doctorRepository;
    private DepartmentRepository departmentRepository;


    public DoctorService(DoctorRepository doctorRepository, DepartmentRepository departmentRepository) {
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
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
}
