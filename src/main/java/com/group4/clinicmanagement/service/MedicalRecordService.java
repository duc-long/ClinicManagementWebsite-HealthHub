package com.group4.clinicmanagement.service;


import com.group4.clinicmanagement.dto.MedicalRecordDetailDTO;
import com.group4.clinicmanagement.dto.MedicalRecordListDTO;
import com.group4.clinicmanagement.dto.doctor.MedicalRecordDTO;
import com.group4.clinicmanagement.repository.MedicalRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import com.group4.clinicmanagement.entity.MedicalRecord;
import java.time.LocalDateTime;
import java.util.List;


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

    public MedicalRecord saveRecord(MedicalRecord medicalRecord) {
//        return medicalRecordRepository.insertMedicalRecord(
//                medicalRecord.getPatient().getPatientId(),
//                medicalRecord.getAppointment().getAppointmentId(),
//                medicalRecord.getCreatedBy().getUserId(),
//                medicalRecord.getDoctor().getDoctorId(),
//                medicalRecord.getDiagnosis(),
//                medicalRecord.getNotes(),
//                medicalRecord.getStatus().getValue(),
//                LocalDateTime.now()
//        );

        return medicalRecordRepository.save(medicalRecord);
    }

    public int updateRecord(MedicalRecord medicalRecord) {
        return medicalRecordRepository.updateMedicalRecord(
                medicalRecord.getRecordId(),
                medicalRecord.getDiagnosis(),
                medicalRecord.getNotes(),
                medicalRecord.getStatusValue()
        );
    }

    public MedicalRecord findById(int medicalRecordId) {
        return  medicalRecordRepository.findById(medicalRecordId).orElse(null);
    }

    public MedicalRecordDTO findDTOById(int recordId) {
        MedicalRecord record = medicalRecordRepository.findByRecordId(recordId).orElse(null);

        if  (record == null) return null;

        MedicalRecordDTO recordDTO = new MedicalRecordDTO();
        recordDTO.setRecordId(record.getRecordId());
        recordDTO.setDiagnosis(record.getDiagnosis());
        recordDTO.setCreatedAt(record.getCreatedAt());
        recordDTO.setDoctorName(record.getDoctor() != null ? record.getDoctor().getStaff().getFullName() : null);
        recordDTO.setNotes(record.getNotes());
        recordDTO.setRecordStatus(record.getStatus());
        recordDTO.setDoctorId(record.getDoctor() != null ? record.getDoctor().getDoctorId() : 0);
        recordDTO.setPatientId(record.getPatient() != null ? record.getPatient().getPatientId() : 0);
        recordDTO.setAppointmentId(record.getAppointment() != null ? record.getAppointment().getAppointmentId() : 0);
        recordDTO.setPatientName(record.getPatient() != null ? record.getPatient().getFullName() : null);

        return recordDTO;
    }

    public List<MedicalRecord> findMedicalRecordByDoctorIdAndStatus(int doctorId) {
        return medicalRecordRepository.findByDoctorIdAndStatusValue(doctorId, 0);
    }

    // method to find medical record by appointment ID
    public MedicalRecord findByAppointmentId(int appointmentId) {
        return medicalRecordRepository.findMedicalRecordByAppointment_AppointmentId(appointmentId)
                .orElse(null);
    }
}

