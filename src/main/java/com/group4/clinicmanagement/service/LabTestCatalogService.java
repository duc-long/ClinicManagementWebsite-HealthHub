package com.group4.clinicmanagement.service;

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
}
