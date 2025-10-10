package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.repositories.DoctorRepository;
import com.group4.clinicmanagement.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
