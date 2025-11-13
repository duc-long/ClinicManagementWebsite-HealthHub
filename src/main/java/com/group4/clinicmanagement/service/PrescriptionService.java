package com.group4.clinicmanagement.service;


import com.group4.clinicmanagement.dto.PrescriptionDetailDTO;

import com.group4.clinicmanagement.dto.doctor.PrescriptionDTO;
import com.group4.clinicmanagement.entity.*;
import com.group4.clinicmanagement.enums.PrescriptionStatus;
import com.group4.clinicmanagement.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final MedicalRecordService medicalRecordService;
    private final DoctorService doctorService;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               MedicalRecordService medicalRecordService, DoctorService doctorService) {
        this.prescriptionRepository = prescriptionRepository;
        this.medicalRecordService = medicalRecordService;
        this.doctorService = doctorService;

    }

    @Transactional
    public List<PrescriptionDetailDTO> getPrescriptionDetailsByRecordId(Integer recordId) {
        return prescriptionRepository.findByPatientIdAndRecordId(recordId);
    }

    public PrescriptionDTO getPrescriptionDTOByRecordId(Integer recordId) {
        Prescription prescription = prescriptionRepository.findPrescriptionByRecordId(recordId).orElse(null);
        if (prescription == null) {
            return null;
        }

        PrescriptionDTO prescriptionDTO = new PrescriptionDTO();
        prescriptionDTO.setPrescriptionId(prescription.getPrescriptionId());
        prescriptionDTO.setDoctorId(prescription.getDoctor() != null ? prescription.getDoctor().getDoctorId() : 0);
        prescriptionDTO.setDoctorName(prescription.getDoctor() != null ? prescription.getDoctor().getStaff().getFullName() : null);
        prescriptionDTO.setStatus(prescription.getStatus());

        return prescriptionDTO;
    }

    public Prescription findPrescriptionById(int prescriptionId) {
        return prescriptionRepository.findById(prescriptionId).orElse(null);
    }

    public Prescription savePrescription(Prescription prescription) {
        Prescription saved = prescriptionRepository.saveAndFlush(prescription);
        return saved;
    }

    @Transactional
    public Prescription createPrescription(int recordId, int doctorId) {
        MedicalRecord record = medicalRecordService.findById(recordId);
        Doctor doctor = doctorService.findDoctorById(doctorId);

        Prescription prescription = new Prescription();
        prescription.setMedicalRecord(record);
        prescription.setDoctor(doctor);

        // saveAndFlush để chắc chắn id được gán
        return prescriptionRepository.saveAndFlush(prescription);
    }
}

