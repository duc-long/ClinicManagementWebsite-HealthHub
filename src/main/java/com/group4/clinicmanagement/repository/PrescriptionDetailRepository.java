package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.PrescriptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PrescriptionDetailRepository extends JpaRepository<PrescriptionDetail, Integer> {
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO dbo.PrescriptionDetail
        (prescription_id, drug_id, quantity, dosage, frequency, duration_days, instruction)
        VALUES (:prescriptionId, :drugId, :quantity, :dosage, :frequency, :durationDay, :instruction)
        """, nativeQuery = true)
    void insertPrescriptionDetail(@Param("prescriptionId") int prescriptionId,
                                  @Param("drugId") int drugId,
                                  @Param("quantity") int quantity,
                                  @Param("dosage") String dosage,
                                  @Param("frequency") int frequency,
                                  @Param("durationDay") int durationDay,
                                  @Param("instruction") String instruction);
}
