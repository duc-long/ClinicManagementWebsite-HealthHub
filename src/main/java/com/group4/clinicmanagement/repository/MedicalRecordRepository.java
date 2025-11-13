package com.group4.clinicmanagement.repository;


import com.group4.clinicmanagement.dto.MedicalRecordDetailDTO;
import com.group4.clinicmanagement.dto.MedicalRecordListDTO;
import com.group4.clinicmanagement.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import com.group4.clinicmanagement.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer> {
    @Query("SELECT new com.group4.clinicmanagement.dto.MedicalRecordListDTO(" +
            "mr.recordId, mr.diagnosis, mr.createdAt, u.fullName AS doctorFullName) " +
            "FROM MedicalRecord mr " +
            "JOIN mr.doctor d " +
            "JOIN d.staff u " +  // JOIN với bảng User để lấy fullName của bác sĩ
            "JOIN mr.patient p " +
            "WHERE p.patientId = :patientId")
    List<MedicalRecordListDTO> findMedicalRecordsByPatientId(@Param("patientId") Integer patientId);

    @Query("SELECT new com.group4.clinicmanagement.dto.MedicalRecordDetailDTO(" +
            "p.fullName, p.email, p.phone, p.genderValue, " + // UserPatient
            "p.dateOfBirth, p.address, " + // Patient
            "d.staff.fullName, " + // UserDoctor
            "d.specialty, " + // Doctor
            "vs.heightCm, vs.weightKg, vs.bloodPressure, vs.heartRate, vs.temperature, " + // VitalSign
            "mr.diagnosis, mr.notes, mr.createdAt) " + // DrugCatalog
            "FROM MedicalRecord mr " +
            "JOIN mr.patient p " +
            "JOIN mr.doctor d " +
            "LEFT JOIN mr.vitalSigns vs " + // VitalSign
            "LEFT JOIN mr.labRequest lrq " + // LabRequest
            "LEFT JOIN lrq.test ltc " + // LabTestCatalog
            "LEFT JOIN mr.prescription pr " + // Prescription
            "WHERE p.patientId = :patientId "+
            "and mr.recordId = :recordId")
    Optional<MedicalRecordDetailDTO> findMedicalRecordDetailByPatientId(@Param("patientId") Integer patientId, @Param("recordId") Integer recordId);


    // method to save medical record
    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO MedicalRecord 
                (patient_id, appointment_id, created_by, doctor_id, diagnosis, notes, status, created_at)
            VALUES 
                (:patientId, :appointmentId, :createdBy, :doctorId, :diagnosis, :notes, :statusValue, :createdAt)
            """, nativeQuery = true)
    int insertMedicalRecord(@Param("patientId") int patientId,
                            @Param("appointmentId") int appointmentId,
                            @Param("createdBy") int createdBy,
                            @Param("doctorId") int doctorId,
                            @Param("diagnosis") String diagnosis,
                            @Param("notes") String notes,
                            @Param("statusValue") int statusValue,
                            @Param("createdAt") LocalDateTime createdAt);

    // method to update medical record
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE MedicalRecord
        SET diagnosis = :diagnosis,
            notes = :notes,
            status = :statusValue
        WHERE record_id = :recordId
        """, nativeQuery = true)
    int updateMedicalRecord(@Param("recordId") int recordId,
                            @Param("diagnosis") String diagnosis,
                            @Param("notes") String notes,
                            @Param("statusValue") int statusValue);

    // method to get list medical record by doctor ID and record status
    @Query(value = "SELECT * FROM MedicalRecord WHERE doctor_id = :doctorId AND status = :status", nativeQuery = true)
    List<MedicalRecord> findByDoctorIdAndStatusValue(@Param("doctorId") int doctorId,
                                                     @Param("status") int statusValue);

    @Query(value = "SELECT * FROM dbo.MedicalRecord WHERE record_id = :id", nativeQuery = true)
    Optional<MedicalRecord> findByRecordId(@Param("id") int id);

    Optional<MedicalRecord> findMedicalRecordByAppointment_AppointmentId(int appointmentId);
}

