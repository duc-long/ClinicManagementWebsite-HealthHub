package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    private DoctorRepository doctorRepository;


        public DoctorService(DoctorRepository doctorRepository) {
            this.doctorRepository = doctorRepository;
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

    }
