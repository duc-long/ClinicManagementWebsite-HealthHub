package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.doctor.PrescriptionDetailDTO;
import com.group4.clinicmanagement.entity.DrugCatalog;
import com.group4.clinicmanagement.entity.Prescription;
import com.group4.clinicmanagement.entity.PrescriptionDetail;
import com.group4.clinicmanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PrescriptionDetailService {
    private final PrescriptionDetailRepository prescriptionDetailRepository;
    private final DrugCatalogRepository drugCatalogRepository;
    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionDetailService(PrescriptionDetailRepository prescriptionDetailRepository,
                                     DrugCatalogRepository drugCatalogRepository, PrescriptionRepository prescriptionRepository) {
        this.prescriptionDetailRepository = prescriptionDetailRepository;
        this.drugCatalogRepository = drugCatalogRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    // method to create PrescriptionDetail
    @Transactional
    public PrescriptionDetail createDetail(int prescriptionId, int drugId, int quantity,
                                           String dosage, String frequency,
                                           int duration, String instruction) {

        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        DrugCatalog drug = drugCatalogRepository.findById(drugId)
                .orElseThrow(() -> new RuntimeException("Drug not found"));

        PrescriptionDetail detail = new PrescriptionDetail();
        detail.setPrescription(prescription);
        detail.setDrug(drug);
        detail.setQuantity(quantity);
        detail.setDosage(dosage);
        detail.setFrequency(frequency);
        detail.setDurationDays(duration);
        detail.setInstruction(instruction);

        return prescriptionDetailRepository.save(detail);
    }

    // method to update PrescriptionDetail
    @Transactional
    public PrescriptionDetail updateDetail(int detailId, int drugId, int quantity,
                                           String dosage, String frequency,
                                           int duration, String instruction) {

        PrescriptionDetail detail = prescriptionDetailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("PrescriptionDetail not found"));

        DrugCatalog drug = drugCatalogRepository.findById(drugId)
                .orElseThrow(() -> new RuntimeException("Drug not found"));

        detail.setDrug(drug);
        detail.setQuantity(quantity);
        detail.setDosage(dosage);
        detail.setFrequency(frequency);
        detail.setDurationDays(duration);
        detail.setInstruction(instruction);

        return prescriptionDetailRepository.save(detail);
    }

    // method to delete PrescriptionDetail
    @Transactional
    public void deleteDetail(int detailId) {
        if (!prescriptionDetailRepository.existsById(detailId)) {
            throw new RuntimeException("PrescriptionDetail not found");
        }
        prescriptionDetailRepository.deleteById(detailId);
    }

    @Transactional
    public void saveOrUpdate(int prescriptionId,
                             List<Integer> detailIds,
                             List<Integer> drugIds,
                             List<Integer> quantities,
                             List<String> dosages,
                             List<String> frequencies,
                             List<Integer> durationDays,
                             List<String> instructions) {

        List<PrescriptionDetail> existingDetails =
                prescriptionDetailRepository.findByPrescription_PrescriptionId(prescriptionId);

        List<Integer> keepIds = new ArrayList<>();

        int size = drugIds.size();
        for (int i = 0; i < size; i++) {
            Integer detailId = (detailIds != null && i < detailIds.size()) ? detailIds.get(i) : null;
            PrescriptionDetail detail;

            if (detailId == null || detailId == 0) {
                // craete new
                detail = createDetail(prescriptionId, drugIds.get(i), quantities.get(i),
                        dosages.get(i), frequencies.get(i),
                        durationDays.get(i), instructions.get(i));
            } else {
                // update
                detail = updateDetail(detailId, drugIds.get(i), quantities.get(i),
                        dosages.get(i), frequencies.get(i),
                        durationDays.get(i), instructions.get(i));
            }

            keepIds.add(detail.getDetailId());
        }

        // delete detail that not exist on prescription
        for (PrescriptionDetail d : existingDetails) {
            if (!keepIds.contains(d.getDetailId())) {
                prescriptionDetailRepository.delete(d);
            }
        }
    }

    @Transactional
    public void addDetails(int prescriptionId,
                           List<Integer> drugIds,
                           List<Integer> quantities,
                           List<String> dosages,
                           List<String> frequencies,
                           List<Integer> durationDays,
                           List<String> instructions) {

        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found: " + prescriptionId));

        if (drugIds == null || drugIds.isEmpty()) return;

        for (int i = 0; i < drugIds.size(); i++) {
            Integer drugId = drugIds.get(i);
            if (drugId == null) continue;

            // lookup drug, skip nếu không tồn tại
            DrugCatalog drug = drugCatalogRepository.findById(drugId).orElse(null);
            if (drug == null) continue;

            Integer qty = (quantities != null && i < quantities.size()) ? quantities.get(i) : 1;
            String dosage = (dosages != null && i < dosages.size()) ? dosages.get(i) : "";
            String freq = (frequencies != null && i < frequencies.size()) ? frequencies.get(i) : "";
            Integer duration = (durationDays != null && i < durationDays.size()) ? durationDays.get(i) : 0;
            String instr = (instructions != null && i < instructions.size()) ? instructions.get(i) : "";

            PrescriptionDetail detail = new PrescriptionDetail();
            detail.setPrescription(prescription);
            detail.setDrug(drug);
            detail.setQuantity(qty);
            detail.setDosage(dosage);
            detail.setFrequency(freq);

            // giữ nguyên setter bạn đang dùng
            detail.setDurationDays(duration); // nếu entity của bạn là setDurationDays thì đổi lại
            detail.setInstruction(instr);

            prescriptionDetailRepository.save(detail);
        }
    }

    public List<PrescriptionDetailDTO> getDetailsDTOByPrescriptionID(int prescriptionId) {
        // check null list
        Prescription prescription = prescriptionRepository.findById(prescriptionId).orElse(null);
        if (prescription == null) {
            return null;
        }

        // get list prescription detail
        List<PrescriptionDetail> details = prescription.getDetails();

        return details.stream()
                .map(detail -> {
                    PrescriptionDetailDTO dto = new PrescriptionDetailDTO();
                    dto.setPrescriptionDetailId(detail.getDetailId());
                    dto.setPrescriptionId(prescription.getPrescriptionId());
                    dto.setDrugId(detail.getDrug() != null ? detail.getDrug().getDrugId() : 0);
                    dto.setDrugName(detail.getDrug() != null ? detail.getDrug().getName() : null);
                    dto.setDosage(detail.getDosage());
                    dto.setQuantity(detail.getQuantity() != null ? detail.getQuantity() : 0);
                    dto.setDuration(detail.getDurationDays() != null ? detail.getDurationDays() : 0);
                    dto.setInstruction(detail.getInstruction());
                    dto.setFrequency(detail.getFrequency());
                    return dto;
                })
                .toList();
    }
}
