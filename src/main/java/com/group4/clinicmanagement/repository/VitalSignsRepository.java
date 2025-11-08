package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.VitalSigns;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface VitalSignsRepository extends JpaRepository<VitalSigns, Integer> {
    @Query("SELECT v FROM VitalSigns v WHERE v.medicalRecord.recordId = :recordId")
    Optional<VitalSigns> findByRecordId(@Param("recordId") Integer recordId);

    Optional<VitalSigns> findByVitalId(Integer vitalId);
}
