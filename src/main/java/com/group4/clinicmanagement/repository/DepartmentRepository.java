package com.group4.clinicmanagement.repository;

import com.group4.clinicmanagement.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    Optional<Department> findByDepartmentId(int departmentId);
}
