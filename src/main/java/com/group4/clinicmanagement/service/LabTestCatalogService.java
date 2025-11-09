package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.doctor.LabTestCatalogDTO;
import com.group4.clinicmanagement.entity.LabTestCatalog;
import com.group4.clinicmanagement.repository.LabTestCatalogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabTestCatalogService {
    LabTestCatalogRepository labTestCatalogRepository;

    public LabTestCatalogService(LabTestCatalogRepository labTestCatalogRepository) {
        this.labTestCatalogRepository = labTestCatalogRepository;
    }

    public List<LabTestCatalog> getAll() {
        return labTestCatalogRepository.findAll();
    }

    // method to get all lab test with DTO
    public List<LabTestCatalogDTO> getAllLabTestDTO() {
        return labTestCatalogRepository.findAll()
                .stream()
                .map(lab -> new LabTestCatalogDTO(
                        lab.getTestId(),
                        lab.getName(),
                        lab.getDescription(),
                        lab.getCost(),
                        lab.getStatus(),
                        lab.getCreatedAt()
                ))
                .toList();
    }

    // method to find lab test bt ID
    public LabTestCatalog findByTestId(Integer testId) {
        return labTestCatalogRepository.findById(testId).orElse(null);
    }

    public LabTestCatalogDTO getLabTestCatalogDTOByTestId(Integer testId) {
        LabTestCatalog labTestCatalog = labTestCatalogRepository.findById(testId).orElse(null);
        if (labTestCatalog == null) {
            return null;
        }

        LabTestCatalogDTO dto = new LabTestCatalogDTO();
        dto.setTestId(testId);
        dto.setName(labTestCatalog.getName());
        dto.setCost(labTestCatalog.getCost());
        dto.setStatus(labTestCatalog.getStatus());
        dto.setCreatedAt(labTestCatalog.getCreatedAt());
        dto.setDescription(labTestCatalog.getDescription());

        return dto;
    }
}
