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

    public DepartmentDTO toDTO(Department department) {
        if (department == null) return null;

        return new DepartmentDTO(
                department.getDepartmentId(),
                department.getName(),
                department.getDescription()
        );
    }

    public List<DepartmentDTO> findAll() {
        List<Department> departments = departmentRepository.findAll();
        List<DepartmentDTO> result = new ArrayList<>();

        for (Department department : departments) {
            DepartmentDTO dto = toDTO(department);
            result.add(dto);
        }
        return result;
    }
}
