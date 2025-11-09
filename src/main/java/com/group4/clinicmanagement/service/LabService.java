package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.LabDTO;
import com.group4.clinicmanagement.repository.LabRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabService {
    private final LabRepository labRepository;
    public LabService(LabRepository labRepository) {
        this.labRepository = labRepository;
    }

    public List<LabDTO> findLabResultByRecordId(Integer recordId) {
        return labRepository.findLabDetailsByMedicalRecordId(recordId);
    }


}