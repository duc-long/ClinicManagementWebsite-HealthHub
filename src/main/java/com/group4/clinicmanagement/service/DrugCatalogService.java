package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.DrugCatalog;
import com.group4.clinicmanagement.repository.DrugCatalogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DrugCatalogService {
    private final DrugCatalogRepository drugCatalogRepository;

    public DrugCatalogService(DrugCatalogRepository drugCatalogRepository) {
        this.drugCatalogRepository = drugCatalogRepository;
    }

    public List<DrugCatalog> findAllDrugs() {
        return drugCatalogRepository.findAllDrugs();
    }

    public DrugCatalog findById(int id) {
        return drugCatalogRepository.findById(id)
                .orElse(null);
    }
}
