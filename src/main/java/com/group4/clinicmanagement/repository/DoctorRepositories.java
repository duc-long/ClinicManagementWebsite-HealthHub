package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepositories extends JpaRepository<Doctor, Integer> {
    @Query("SELECT DISTINCT d.specialty FROM Doctor d")
    List<String> findAllDistinctSpecialties();

    @Query("SELECT d FROM Doctor d JOIN d.user u WHERE " +
            "(:name = '' OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:specialty = 'All' OR d.specialty = :specialty)")
    List<Doctor> findDoctorByNameAndSpecialty(@Param("name") String name, @Param("specialty") String specialty);

    Doctor getDoctorByDoctorId(Integer doctorId);

    List<Doctor> getDoctorBySpecialtyIgnoreCase(String specialty);
}
