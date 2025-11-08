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
        if (LabId == null) {return null;}
        LabRequest labRequest = labRequestRepository.findById(LabId).orElse(null);

        if (labRequest == null) {return null;}
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

}
