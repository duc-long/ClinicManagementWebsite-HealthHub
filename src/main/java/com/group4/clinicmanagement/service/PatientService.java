package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.repository.DoctorRepository;
import com.group4.clinicmanagement.repository.PatientRepository;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    PatientRepository patientRepository;

    DoctorRepository doctorRepository;

    public PatientService(PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public Patient getPatientById(int patientId) {
        return patientRepository.findById(patientId).get();
    }
}
