package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.Prescription;
import com.group4.clinicmanagement.entity.PrescriptionDetail;
import com.group4.clinicmanagement.enums.PrescriptionStatus;
import com.group4.clinicmanagement.repository.PrescriptionDetailRepository;
import com.group4.clinicmanagement.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionDetailRepository prescriptionDetailRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository, PrescriptionDetailRepository prescriptionDetailRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionDetailRepository = prescriptionDetailRepository;
    }

    public Prescription findById(int prescriptionId) {
        return prescriptionRepository.findById(prescriptionId).orElse(null);
    }

    @Transactional
    public void savePrescription(int recordId, int doctorId, PrescriptionStatus status,
                                    List<Integer> drugIds, List<Integer> quantities,
                                    List<String> dosages, List<Integer> frequencies,
                                    List<Integer> durationDays, List<String> instructions) {

        // 🟢 Insert Prescription
        prescriptionRepository.insertPrescription(recordId, doctorId, status.getValue());

        // 🟢 Lấy ID vừa insert
        Integer prescriptionId = prescriptionRepository.findLastInsertedId();

        // 🟢 Insert các chi tiết thuốc
        for (int i = 0; i < drugIds.size(); i++) {
            prescriptionDetailRepository.insertPrescriptionDetail(
                    prescriptionId,
                    drugIds.get(i),
                    quantities.get(i),
                    dosages.get(i),
                    frequencies.get(i),
                    durationDays.get(i),
                    instructions.get(i)
            );
        }
    }
}
