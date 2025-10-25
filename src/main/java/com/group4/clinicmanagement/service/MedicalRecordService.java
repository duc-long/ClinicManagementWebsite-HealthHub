package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.MedicalRecordDetailDTO;
import com.group4.clinicmanagement.dto.MedicalRecordListDTO;
import com.group4.clinicmanagement.repository.MedicalRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;
    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Transactional
    // Phương thức để lấy medical records theo patientId
    public List<MedicalRecordListDTO> getMedicalRecordsByPatientId(Integer patientId) {
        // Gọi repository để thực thi câu truy vấn và trả về danh sách các MedicalRecordDTO
        return medicalRecordRepository.findMedicalRecordsByPatientId(patientId);
    }

    @Transactional
    public Optional<MedicalRecordDetailDTO> getMedicalRecordDetailsByPatientId(Integer patientId, Integer recordId) {
        return medicalRecordRepository.findMedicalRecordDetailByPatientId(patientId, recordId);
    }
}