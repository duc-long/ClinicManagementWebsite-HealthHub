package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.Department;
import com.group4.clinicmanagement.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> getAllDepartment(){
        List<Department> list = departmentRepository.findAll();
        return list;
    }
}
