package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer> {
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

    // method to get list medical record by doctor ID and record status
    @Query(value = "SELECT * FROM MedicalRecord WHERE doctor_id = :doctorId AND status = :status", nativeQuery = true)
    List<MedicalRecord> findByDoctorIdAndStatusValue(@Param("doctorId") int doctorId,
                                                     @Param("status") int statusValue);

    @Query(value = "SELECT * FROM dbo.MedicalRecord WHERE record_id = :id", nativeQuery = true)
    Optional<MedicalRecord> findByRecordId(@Param("id") int id);
}
