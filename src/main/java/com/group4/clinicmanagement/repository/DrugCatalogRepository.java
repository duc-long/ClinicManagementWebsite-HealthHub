package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.DrugCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface DrugCatalogRepository extends JpaRepository<DrugCatalog, Integer> {
    @Query(value = "SELECT * FROM DrugCatalog", nativeQuery = true)
    List<DrugCatalog> findAllDrugs();

    @Query(value = "SELECT * FROM DrugCatalog WHERE drug_id = :id", nativeQuery = true)
    Optional<DrugCatalog>  findById(@Param("id") Integer id);
}
