package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.ReceptionistUserDTO;
import com.group4.clinicmanagement.dto.UserDTO;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.enums.Gender;
import com.group4.clinicmanagement.repository.ReceptionistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReceptionistService {
    private final ReceptionistRepository receptionistRepository;

    public ReceptionistService(ReceptionistRepository receptionistRepository) {
        this.receptionistRepository = receptionistRepository;
    }

    private ReceptionistUserDTO convertToDTO(User user) {
        if (user == null) return null;
        return new ReceptionistUserDTO(
               user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getGender(),
                user.getAvatar(),
                user.getStatus()
        );
    }

    private void applyDTOToEntity(ReceptionistUserDTO dto, User user) {
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender());
    }

    @Transactional(readOnly = true)
    public ReceptionistUserDTO getReceptionistProfile(String receptionistName) {
        User user = receptionistRepository.findByUsername(receptionistName);
        System.out.println("\n====="+ user.getUsername());
        return convertToDTO(user);
    }

    @Transactional
    public void updateReceptionistProfile(String receptionistName, ReceptionistUserDTO dto) {
        User user = receptionistRepository.findByUsername(receptionistName);
        System.out.println("\n====="+ dto.getUsername());
        applyDTOToEntity(dto, user);
        receptionistRepository.save(user);
    }
}
