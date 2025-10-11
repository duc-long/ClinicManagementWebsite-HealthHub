package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.repository.DoctorRepository;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {
    DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public Doctor findByDoctorId(Integer doctorId) {
        return doctorRepository.findById(doctorId).get();
    }
}
