package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.VitalSignsDTO;
import com.group4.clinicmanagement.entity.Appointment;
import com.group4.clinicmanagement.entity.Doctor;
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
    public VitalSigns saveOrUpdate(int recordId, VitalSignsDTO vitalInputDTO, Doctor doctor) {
        MedicalRecord medicalRecord = medicalRecordRepository.findByRecordId(recordId).orElse(null);
        if (medicalRecord == null || doctor == null) {
            return null;
        }

        VitalSigns existing = vitalSignsRepository.findByRecordId(recordId).orElse(null);

        if (existing == null) {
            VitalSigns vitalSigns = new VitalSigns();
            vitalSigns.setDoctor(doctor);
            vitalSigns.setMedicalRecord(medicalRecord);
            vitalSigns.setTemperature(vitalInputDTO.getTemperature());
            vitalInputDTO.updateBloodPressure();
            vitalSigns.setBloodPressure(vitalInputDTO.getBloodPressure());
            vitalSigns.setHeartRate(vitalInputDTO.getHeartRate());
            vitalSigns.setHeightCm(vitalInputDTO.getHeightCm());
            vitalSigns.setWeightKg(vitalInputDTO.getWeightKg());
            return vitalSignsRepository.save(vitalSigns);
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

    // method to get vital signs dto
    public VitalSignsDTO findVitalSignsDTOById(int recordId) {
        VitalSigns vitalSigns = vitalSignsRepository.findByRecordId(recordId).orElse(null);

        VitalSignsDTO vitalSignsDTO = new VitalSignsDTO();
        if (vitalSigns != null) {
            vitalSignsDTO.setVitalId(vitalSigns.getVitalId());
            vitalSignsDTO.setRecordId(recordId);
            vitalSignsDTO.setHeightCm(vitalSigns.getHeightCm());
            vitalSignsDTO.setWeightKg(vitalSigns.getWeightKg());
            vitalSignsDTO.setBloodPressure(vitalSigns.getBloodPressure());
            vitalSignsDTO.setHeartRate(vitalSigns.getHeartRate());
            vitalSignsDTO.setTemperature(vitalSigns.getTemperature());
            vitalSignsDTO.setRecordedAt(vitalSigns.getRecordedAt());

            // split systolic/diastolic
            String bp = vitalSigns.getBloodPressure();
            if (bp != null && bp.contains("/")) {
                try {
                    String[] parts = bp.split("/");
                    vitalSignsDTO.setSystolic(Integer.parseInt(parts[0].trim()));
                    vitalSignsDTO.setDiastolic(Integer.parseInt(parts[1].trim()));
                } catch (Exception ignored) {
                }
            }
        } else {
            return null;
        }

        return vitalSignsDTO;
    }

    // method to get vital sign
    public VitalSigns findVitalSignById(int vitalId) {
        return vitalSignsRepository.findByRecordId(vitalId).orElse(null);
    }
}
