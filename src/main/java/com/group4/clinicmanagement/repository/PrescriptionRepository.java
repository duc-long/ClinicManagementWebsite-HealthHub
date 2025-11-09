package com.group4.clinicmanagement.repository;


import com.group4.clinicmanagement.dto.PrescriptionDetailDTO;
import com.group4.clinicmanagement.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Integer> {

    @Query("SELECT new com.group4.clinicmanagement.dto.PrescriptionDetailDTO(" +
            "dc.name, pd.quantity, pd.dosage, pd.frequency, pd.duration_days, pd.instruction) " +
            "FROM Prescription p " +
            "JOIN p.details pd " +
            "JOIN pd.drug dc " +
            "WHERE p.medicalRecord.recordId = :recordId")
    List<PrescriptionDetailDTO> findByPatientIdAndRecordId(@Param("recordId") Integer recordId);

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO dbo.Prescription (record_id, doctor_id, status, prescribed_at)
        VALUES (:recordId, :doctorId, :statusValue, GETDATE());
        """, nativeQuery = true)
    void insertPrescription(@Param("recordId") int recordId,
                            @Param("doctorId") int doctorId,
                            @Param("statusValue") int statusValue);

    @Query(value = "SELECT TOP 1 prescription_id FROM dbo.Prescription ORDER BY prescription_id DESC", nativeQuery = true)
    int findLastInsertedId();

    @Query(value = "SELECT * FROM Prescription WHERE record_id = :id", nativeQuery = true)
    Optional<Prescription> findPrescriptionByRecordId(@Param("id") Integer recordId);
}

