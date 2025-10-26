package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.MedicalRecord;
import com.group4.clinicmanagement.repository.MedicalRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    public int saveRecord(MedicalRecord medicalRecord) {
        return medicalRecordRepository.insertMedicalRecord(
                medicalRecord.getPatient().getPatientId(),
                medicalRecord.getAppointment().getAppointmentId(),
                medicalRecord.getCreatedBy().getUserId(),
                medicalRecord.getDoctor().getDoctorId(),
                medicalRecord.getDiagnosis(),
                medicalRecord.getNotes(),
                medicalRecord.getStatus().getValue(),
                LocalDateTime.now()
        );
    }

    public MedicalRecord findById(int recordId) {
        return  medicalRecordRepository.findByRecordId(recordId).orElse(null);
    }

    public List<MedicalRecord> findMedicalRecordByDoctorIdAndStatus(int doctorId) {
        return medicalRecordRepository.findByDoctorIdAndStatusValue(doctorId, 0);
    }
}
