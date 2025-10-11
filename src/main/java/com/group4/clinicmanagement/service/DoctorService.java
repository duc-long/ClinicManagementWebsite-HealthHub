package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.repository.DoctorRepositories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    private DoctorRepositories doctorRepositories;

    public DoctorService(DoctorRepositories doctorRepositories) {
        this.doctorRepositories = doctorRepositories;
    }

    public List<Doctor> findAllDoctors() {
        return doctorRepositories.findAll();
    }

    public List<String> findAllDistinctSpecialties() {
    return   doctorRepositories.findAllDistinctSpecialties();
    }

    public List<Doctor> findDoctorByNameAndSpecialty(String name, String specialty) {
        return doctorRepositories.findDoctorByNameAndSpecialty(name, specialty);

    }

    public Doctor findDoctorById(int id) {
        return doctorRepositories.getDoctorByDoctorId(id);
    }
}
