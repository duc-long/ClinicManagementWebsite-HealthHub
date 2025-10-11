package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.dto.MedicalRecordListDTO;
import com.group4.clinicmanagement.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRecordListRepository extends JpaRepository<MedicalRecord, Integer> {
    @Query("SELECT new com.group4.clinicmanagement.dto.MedicalRecordListDTO(" +
            "mr.diagnosis, mr.createdAt, u.fullName AS doctorFullName) " +
            "FROM MedicalRecord mr " +
            "JOIN mr.doctor d " +
            "JOIN d.user u " +  // JOIN với bảng User để lấy fullName của bác sĩ
            "JOIN mr.patient p " +
            "WHERE p.patientId = :patientId")
    List<MedicalRecordListDTO> findMedicalRecordsByPatientId(@Param("patientId") Integer patientId);
}
