package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.DepartmentDTO;
import com.group4.clinicmanagement.dto.admin.DoctorDTO;
import com.group4.clinicmanagement.entity.Department;
import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    private DepartmentDTO mapToDepartmentDTO(Department department) {
        return new DepartmentDTO(
                department.getDepartmentId(),
                department.getName(),
                department.getDescription()
        );
    }

    @Transactional
    public List<DepartmentDTO> findAllDepartment() {
        List<Department> departments = departmentRepository.findAll();

        List<DepartmentDTO> departmentDTOs = new ArrayList<>();
        for (Department department : departments) {
            departmentDTOs.add(mapToDepartmentDTO(department));
        }
        return departmentDTOs;
    }
}
