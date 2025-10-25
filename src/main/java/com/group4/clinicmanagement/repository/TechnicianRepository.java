package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechnicianRepository extends JpaRepository<User, Integer> {

}
