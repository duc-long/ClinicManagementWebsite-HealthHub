package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechnicianRepository extends JpaRepository<Staff, Integer> {

}
