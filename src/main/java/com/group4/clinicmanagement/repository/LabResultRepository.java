package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.LabResult;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LabResultRepository extends JpaRepository<LabResult,Integer> {

    @EntityGraph(attributePaths = {"images", "labRequest"})
    Optional<LabResult> findById(Integer id);

    List<LabResult> findAll();
    @Query(value = """
        SELECT r.*
        FROM LabResult r
        JOIN LabRequest lr ON r.lab_request_id = lr.lab_request_id
        JOIN LabTestCatalog t ON lr.test_id = t.test_id
        WHERE ((:resultId IS NULL OR :resultId = '') OR CAST(r.result_id AS NVARCHAR) LIKE CONCAT('%', :resultId, '%'))
          AND ((:testName IS NULL OR :testName = '') OR LOWER(t.name) LIKE LOWER(CONCAT('%', :testName, '%')))
          AND ((:date IS NULL) OR CAST(r.created_at AS DATE) = :date)
        ORDER BY r.created_at DESC
    """, nativeQuery = true)
    List<LabResult> filterResults(
            @Param("resultId") String resultId,
            @Param("testName") String testName,
            @Param("date") LocalDate date
    );


}
