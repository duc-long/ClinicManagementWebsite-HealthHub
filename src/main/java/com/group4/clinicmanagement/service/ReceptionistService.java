package com.group4.clinicmanagement.service;

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

    private UserDTO convertToDTO(User user) {
        if (user == null) return null;
        return new UserDTO(
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getGender(),
                user.getUserId()
        );
    }

    private void applyDTOToEntity(UserDTO dto, User user) {
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender());
    }

    @Transactional(readOnly = true)
    public UserDTO getReceptionistProfile(int id) {
        User user = receptionistRepository.findById(id).orElse(null);
        return convertToDTO(user);
    }

    @Transactional
    public void updateReceptionistProfile(UserDTO dto) {
        User user = receptionistRepository.findById(dto.getId()).orElseThrow();
        applyDTOToEntity(dto, user);
        receptionistRepository.save(user);
    }
}
