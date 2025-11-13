package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.ReceptionistUserDTO;
import com.group4.clinicmanagement.entity.Staff;
import com.group4.clinicmanagement.repository.ReceptionistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReceptionistService {
    private final ReceptionistRepository receptionistRepository;

    public ReceptionistService(ReceptionistRepository receptionistRepository) {
        this.receptionistRepository = receptionistRepository;
    }

    private ReceptionistUserDTO convertToDTO(Staff user) {
        if (user == null) return null;
        return new ReceptionistUserDTO(
               user.getStaffId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getGender(),
                user.getAvatar(),
                user.getStatus()
        );
    }

    private void applyDTOToEntity(ReceptionistUserDTO dto, Staff user) {
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender());
    }

    @Transactional(readOnly = true)
    public ReceptionistUserDTO getReceptionistProfile(String receptionistName) {
        Staff user = receptionistRepository.findByUsername(receptionistName);
        return convertToDTO(user);
    }

    @Transactional
    public void updateReceptionistProfile(String receptionistName, ReceptionistUserDTO dto) {
        Staff user = receptionistRepository.findByUsername(receptionistName);
        applyDTOToEntity(dto, user);
        receptionistRepository.save(user);
    }
}