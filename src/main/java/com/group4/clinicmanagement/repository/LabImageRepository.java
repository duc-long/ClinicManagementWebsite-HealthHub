package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.LabImage;
import com.group4.clinicmanagement.entity.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabImageRepository extends JpaRepository<LabImage, Integer> {
    List<LabImage> findByLabResult(LabResult result);

    @Modifying
    @Query("DELETE FROM LabImage l WHERE l.filePath = :filePath")
    void deleteLabImageByFilePath(@Param("filePath") String filePath);


}
