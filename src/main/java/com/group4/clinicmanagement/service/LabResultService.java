package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.LabResult;
import com.group4.clinicmanagement.repository.LabResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Service

public class LabResultService {
    LabResultRepository labResultRepository;

    public LabResultService(LabResultRepository labResultRepository) {
        this.labResultRepository = labResultRepository;
    }

    public List<LabResult> findLabResultList() {
        return labResultRepository.findAll();
    }

    public List<LabResult> filterResults(String resultId, String testName, LocalDate date) {
        return labResultRepository.filterResults(resultId, testName, date);
    }

    public LabResult findById(Integer id) {
        return labResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Result not found"));
    }
}
