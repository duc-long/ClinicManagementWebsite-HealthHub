package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.dto.PrescriptionDetailDTO;
import com.group4.clinicmanagement.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Integer> {

    @Query("SELECT new com.group4.clinicmanagement.dto.PrescriptionDetailDTO(" +
            "dc.name, pd.quantity, pd.dosage, pd.frequency, pd.duration_days, pd.instruction) " +
            "FROM Prescription p " +
            "JOIN p.details pd " +
            "JOIN pd.drug dc " +
            "WHERE p.medicalRecord.recordId = :recordId")
    List<PrescriptionDetailDTO> findByPatientIdAndRecordId(@Param("recordId") Integer recordId);
}