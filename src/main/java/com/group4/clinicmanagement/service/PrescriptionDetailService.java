package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.PrescriptionDetail;
import com.group4.clinicmanagement.repository.PrescriptionDetailRepository;
import org.springframework.stereotype.Service;

@Service
public class PrescriptionDetailService {
    private final PrescriptionDetailRepository prescriptionDetailRepository;

    public PrescriptionDetailService(PrescriptionDetailRepository prescriptionDetailRepository) {
        this.prescriptionDetailRepository = prescriptionDetailRepository;
    }

    public PrescriptionDetail findPrescriptionById(int prescriptionId) {
        return  prescriptionDetailRepository.findById(prescriptionId).orElse(null);
    }
}
