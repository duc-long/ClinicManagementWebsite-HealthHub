package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.MedicalRecordListDTO;
import com.group4.clinicmanagement.repository.MedicalRecordListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicalRecordListService {
    private final MedicalRecordListRepository medicalRecordListRepository;
    public MedicalRecordListService(MedicalRecordListRepository medicalRecordListRepository) {
        this.medicalRecordListRepository = medicalRecordListRepository;
    }

    @Transactional
    // Phương thức để lấy medical records theo patientId
    public List<MedicalRecordListDTO> getMedicalRecordsByPatientId(Integer patientId) {
        // Gọi repository để thực thi câu truy vấn và trả về danh sách các MedicalRecordDTO
        return medicalRecordListRepository.findMedicalRecordsByPatientId(patientId);
    }
}
