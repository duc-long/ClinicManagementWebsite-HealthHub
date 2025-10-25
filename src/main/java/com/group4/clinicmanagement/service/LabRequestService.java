package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.LabRequestDTO;
import com.group4.clinicmanagement.entity.LabRequest;
import com.group4.clinicmanagement.repository.LabRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LabRequestService {

    LabRequestRepository labRequestRepository;

    public LabRequestService(LabRequestRepository labRequestRepository) {
        this.labRequestRepository = labRequestRepository;
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

}
