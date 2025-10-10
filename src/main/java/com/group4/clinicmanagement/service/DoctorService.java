package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.repository.DoctorRepositories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    private DoctorRepositories doctorRepositories;

    public DoctorService(DoctorRepositories doctorRepositories) {
        this.doctorRepositories = doctorRepositories;
    }

    public List<Doctor> findAllDoctors() {
        return  doctorRepositories.findAll();
    }

}
