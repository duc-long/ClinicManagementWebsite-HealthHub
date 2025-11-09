package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.CashierLabRequestDTO;
import com.group4.clinicmanagement.dto.LabRequestDTO;
import com.group4.clinicmanagement.entity.Bill;
import com.group4.clinicmanagement.entity.LabRequest;
import com.group4.clinicmanagement.enums.LabRequestStatus;
import com.group4.clinicmanagement.repository.BillRepository;
import com.group4.clinicmanagement.repository.LabRequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LabRequestService {

   private final LabRequestRepository labRequestRepository;
   private final BillRepository billRepository;

    public LabRequestService(LabRequestRepository labRequestRepository, BillRepository billRepository) {
        this.labRequestRepository = labRequestRepository;
        this.billRepository = billRepository;
    }

    public List<LabRequest> getAllLabRequests() {
        return this.labRequestRepository.findAll();
    }

    public LabRequest findLabRequestById(int id) {
        LabRequest labRequest = this.labRequestRepository.findById(id).orElseThrow();
        return labRequestRepository.getReferenceById(id);
    }

    public LabRequestDTO toDTO(LabRequest entity) {
        LabRequestDTO dto = new LabRequestDTO();
        dto.setLabRequestId(entity.getLabRequestId());
        dto.setMedicalRecordId(entity.getMedicalRecord().getRecordId());
        dto.setDoctor(entity.getDoctor());
        dto.setLabTestCatalog(entity.getTest());
        dto.setStatusValue(entity.getStatusValue());
        dto.setStatus(entity.getStatus());
        dto.setRequestedAt(entity.getRequestedAt());
        dto.setPatient(entity.getMedicalRecord().getPatient());
        return dto;
    }

    public List<LabRequestDTO> getAllLabRequestDTO() {
        List<LabRequest>  labRequests = this.labRequestRepository.findAll();
        List<LabRequestDTO> dtoList = new ArrayList<>();

        for (LabRequest entity : labRequests) {
            LabRequestDTO dto = toDTO(entity);
            dtoList.add(dto);
        }

        return dtoList;
    }

    public List<LabRequestDTO> filterRequests(
            String patientId,
            String doctorName,
            String testName,
            String status,
            LocalDateTime fromDate,
            LocalDateTime toDate
    ) {
        List<LabRequest> labRequests = labRequestRepository.filterRequests(patientId, doctorName, testName, status, fromDate, toDate);

        List<LabRequestDTO> dtoList = new ArrayList<>();
        for(LabRequest labRequest : labRequests){
            LabRequestDTO dto = toDTO(labRequest);
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Transactional
    public Page<CashierLabRequestDTO> getStatusLabRequestPage(int statusValue, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        Page<LabRequest> labRequests = labRequestRepository.findByStatus(statusValue, pageable);

        return labRequests.map(a -> {
            Integer billId = billRepository.findByLabRequest_LabRequestId(a.getLabRequestId())
                    .map(Bill::getBillId)
                    .orElse(null);

            boolean canCreateBill = (billId == null);

            return new CashierLabRequestDTO(
                    a.getLabRequestId(),
                    a.getMedicalRecord() != null && a.getMedicalRecord().getPatient() != null
                            && a.getMedicalRecord().getPatient().getUser() != null
                            ? a.getMedicalRecord().getPatient().getUser().getFullName()
                            : "Unknown",
                    a.getDoctor() != null && a.getDoctor().getUser() != null
                            ? a.getDoctor().getUser().getFullName()
                            : "Unknown",
                    a.getTest() != null ? a.getTest().getName() : "N/A",
                    a.getTest() != null ? a.getTest().getCost() : 0,
                    a.getRequestedAt(),
                    LabRequestStatus.fromInt(a.getStatusValue()),
                    billId,
                    canCreateBill
            );
        });
    }

}
