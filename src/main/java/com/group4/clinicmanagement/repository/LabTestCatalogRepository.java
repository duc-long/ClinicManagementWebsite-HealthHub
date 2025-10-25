package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.LabTestCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabTestCatalogRepository extends JpaRepository<LabTestCatalog, Integer> {
}
