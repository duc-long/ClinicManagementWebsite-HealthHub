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

import java.time.LocalDate;
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
        LabRequest labRequest = labRequestRepository.findById(id).orElse(null);
        return labRequest;
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
            boolean isAll
    ) {
        List<LabRequestDTO> dtoList = new ArrayList<>();
        List<LabRequest> labRequests = labRequestRepository.filterRequests(patientId, doctorName, testName, status);
        if (isAll){
            for(LabRequest labRequest : labRequests){
                LabRequestDTO dto = toDTO(labRequest);
                dtoList.add(dto);
            }
            return dtoList;
        }else {
            for(LabRequest labRequest : labRequests){
                if (LocalDate.now().isEqual(labRequest.getRequestedAt().toLocalDate())) {
                    LabRequestDTO dto = toDTO(labRequest);
                    dtoList.add(dto);
                }
            }
            return dtoList;
        }



    }

    // method to save lab request
    public LabRequest saveLabRequest(LabRequest labRequest) {
        return labRequestRepository.save(labRequest);
    }

    // method to check if exist lab request
    public LabRequest isExistLabRequest(int labRequestId) {
        return labRequestRepository.findByRecordId(labRequestId).orElse(null);
    }

    public com.group4.clinicmanagement.dto.doctor.LabRequestDTO findLabRequestDTOById(int labId) {
        LabRequest labRequest = labRequestRepository.findById(labId).orElse(null);

        if (labRequest == null) {
            return null;
        }

        com.group4.clinicmanagement.dto.doctor.LabRequestDTO labRequestDTO =  new com.group4.clinicmanagement.dto.doctor.LabRequestDTO();
        labRequestDTO.setLabRequestId(labRequest.getLabRequestId());
        labRequestDTO.setMedicalRecordId(labRequest.getMedicalRecord().getRecordId());
        labRequestDTO.setLabTestCatalogId(labRequest.getTest().getTestId());
        labRequestDTO.setStatus(labRequest.getStatus());
        labRequestDTO.setDoctorId(labRequest.getDoctor().getDoctorId());
        labRequestDTO.setRequestedAt(labRequest.getRequestedAt());

        return labRequestDTO;
    }

    // method to delete lab request by ID
    public boolean deleteRequestById(Integer requestId) {
        LabRequest labRequest = labRequestRepository.findById(requestId).orElse(null);
        if (labRequest == null) {
            return false;
        }

        labRequestRepository.delete(labRequest);
        return true;
    }

    // method to get lab request DTO
    public com.group4.clinicmanagement.dto.doctor.LabRequestDTO findLabRequestDTOById(Integer LabId) {
        if (LabId == null) {
            return null;
        }
        LabRequest labRequest = labRequestRepository.findById(LabId).orElse(null);

        if (labRequest == null) {
            return null;
        }
        com.group4.clinicmanagement.dto.doctor.LabRequestDTO dto = new com.group4.clinicmanagement.dto.doctor.LabRequestDTO();
        dto.setLabRequestId(labRequest.getLabRequestId());
        dto.setMedicalRecordId(labRequest.getMedicalRecord().getRecordId());
        dto.setDoctorId(labRequest.getDoctor().getDoctorId());
        dto.setLabTestCatalogId(labRequest.getTest().getTestId());
        dto.setStatusValue(labRequest.getStatusValue());
        dto.setStatus(labRequest.getStatus());
        dto.setRequestedAt(labRequest.getRequestedAt());

        return dto;
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
