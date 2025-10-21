package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.LabRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LabRequestRepository extends JpaRepository<LabRequest, Integer> {

    @Query(value = """
    SELECT lr.*
    FROM LabRequest lr
    JOIN MedicalRecord mr ON lr.record_id = mr.record_id
    JOIN Patient p ON mr.patient_id = p.patient_id
    JOIN Doctor d ON lr.doctor_id = d.doctor_id
    JOIN Users u ON d.doctor_id = u.user_id
    JOIN LabTestCatalog t ON lr.test_id = t.test_id
    WHERE ((:patientId IS NULL OR :patientId = '') OR CAST(p.patient_id AS NVARCHAR) LIKE CONCAT('%', :patientId, '%'))
      AND ((:doctorName IS NULL OR :doctorName = '') OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :doctorName, '%')))
      AND ((:testName IS NULL OR :testName = '') OR LOWER(t.name) LIKE LOWER(CONCAT('%', :testName, '%')))
      AND ((:status IS NULL OR :status = '') OR lr.status =
           CASE
               WHEN :status = 'REQUESTED' THEN 0
               WHEN :status = 'PAID' THEN 1
               WHEN :status = 'COMPLETED' THEN 2
               WHEN :status = 'CANCELLED' THEN 3
           END)
      AND ((:fromDate IS NULL) OR lr.requested_at >= :fromDate)
      AND ((:toDate IS NULL) OR lr.requested_at <= :toDate)
    ORDER BY lr.requested_at DESC
""", nativeQuery = true)
    List<LabRequest> filterRequests(
            @Param("patientId") String patientId,
            @Param("doctorName") String doctorName,
            @Param("testName") String testName,
            @Param("status") String status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
