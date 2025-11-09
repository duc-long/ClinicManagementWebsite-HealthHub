package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.dto.LabDTO;
import com.group4.clinicmanagement.entity.LabRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabRepository extends JpaRepository<LabRequest, Integer> {

    @Query("SELECT new com.group4.clinicmanagement.dto.LabDTO(" +
            "ltc.name, ltc.description, lr.resultText, li.filePath, li.description) " +
            "FROM LabRequest lrq " +
            "JOIN lrq.labResults lr " +
            "JOIN lrq.test ltc " +
            "LEFT JOIN lr.images li " +
            "WHERE lrq.medicalRecord.recordId = :recordId")
    List<LabDTO> findLabDetailsByMedicalRecordId(@Param("recordId") Integer recordId);
}