package com.group4.clinicmanagement.service.admin;

import com.group4.clinicmanagement.dto.admin.PatientDTO;
import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.repository.admin.PatientForAdminRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PatientForAdminService {
    private final PatientForAdminRepository patientForAdminRepository;
    public PatientForAdminService(PatientForAdminRepository patientForAdminRepository) {
        this.patientForAdminRepository = patientForAdminRepository;
    }

    private PatientDTO mapToPatientDTO(Patient patient) {
        User user = patient.getUser();

        return new PatientDTO(
                user.getUserId(),
                patient.getPatientId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getGender(),
                patient.getAddress(),
                patient.getUser().getAvatar(),
                patient.getDateOfBirth(),
                user.getStatus(),
                user.getRole().getRoleId()
        );
    }

    public Page<PatientDTO> findAll(Pageable pageable) {
        Page<Patient> patientPage = patientForAdminRepository.findAll(pageable);

        List<PatientDTO> dtoList = new ArrayList<>();
        for (Patient patient : patientPage.getContent()) {
            dtoList.add(mapToPatientDTO(patient));
        }

        return new PageImpl<>(dtoList, pageable, patientPage.getTotalElements());
    }

    public PatientDTO findById(Integer id) {
        Patient patient = patientForAdminRepository.getPatientsByPatientId(id);
        return mapToPatientDTO(patient);
    }




}
