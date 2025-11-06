package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.VitalSignsDTO;
import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.MedicalRecord;
import com.group4.clinicmanagement.entity.VitalSigns;
import com.group4.clinicmanagement.repository.AppointmentRepository;
import com.group4.clinicmanagement.repository.MedicalRecordRepository;
import com.group4.clinicmanagement.repository.VitalSignsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class VitalSignsService {
    private final VitalSignsRepository vitalSignsRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public VitalSignsService(VitalSignsRepository vitalSignsRepository, MedicalRecordRepository medicalRecordRepository) {
        this.vitalSignsRepository = vitalSignsRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    // method to save and update Vital Sign
    @Transactional
    public VitalSigns saveOrUpdate(int recordId, VitalSignsDTO vitalInputDTO) {
        MedicalRecord medicalRecord = medicalRecordRepository.findByRecordId(recordId).orElse(null);
        if (medicalRecord == null) {
            return null;
        }

        VitalSigns existing = vitalSignsRepository.findByRecordId(recordId).orElse(null);

        if (existing == null) {
            VitalSigns vitalSigns = new VitalSigns();
            vitalSigns.setMedicalRecord(medicalRecord);
            vitalSigns.setVitalId(vitalInputDTO.getVitalId());
            vitalSigns.setTemperature(vitalInputDTO.getTemperature());
            vitalSigns.setBloodPressure(vitalInputDTO.getBloodPressure());
            vitalSigns.setHeightCm(vitalInputDTO.getHeightCm());
            vitalSigns.setWeightKg(vitalInputDTO.getWeightKg());
            vitalSignsRepository.save(vitalSigns);
        } else {
            existing.setHeightCm(vitalInputDTO.getHeightCm());
            existing.setWeightKg(vitalInputDTO.getWeightKg());
            vitalInputDTO.updateBloodPressure();
            existing.setBloodPressure(vitalInputDTO.getBloodPressure());
            existing.setHeartRate(vitalInputDTO.getHeartRate());
            existing.setTemperature(vitalInputDTO.getTemperature());
            existing.setRecordedAt(LocalDateTime.now());
            existing.setMedicalRecord(medicalRecord);
            existing.setVitalId(vitalInputDTO.getVitalId());
        }
        return vitalSignsRepository.save(existing);
    }

    public VitalSigns findVitalSignsById(int recordId) {
        return vitalSignsRepository.findByRecordId(recordId).orElse(null);
    }
}
