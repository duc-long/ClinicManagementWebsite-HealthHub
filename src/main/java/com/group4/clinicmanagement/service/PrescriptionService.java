package com.group4.clinicmanagement.service;


import com.group4.clinicmanagement.dto.PrescriptionDetailDTO;

import com.group4.clinicmanagement.dto.doctor.PrescriptionDTO;
import com.group4.clinicmanagement.entity.Prescription;
import com.group4.clinicmanagement.entity.PrescriptionDetail;
import com.group4.clinicmanagement.enums.PrescriptionStatus;
import com.group4.clinicmanagement.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
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
        prescriptionDTO.setDoctorName(prescription.getDoctor() != null ? prescription.getDoctor().getUser().getFullName() : null);
        prescriptionDTO.setStatus(prescription.getStatus());

        return prescriptionDTO;
    }

    public Prescription findPrescriptionById(int prescriptionId) {
        return prescriptionRepository.findById(prescriptionId).orElse(null);
    }

    public void savePrescription(Prescription prescription) {
        prescriptionRepository.save(prescription);
    }
}

