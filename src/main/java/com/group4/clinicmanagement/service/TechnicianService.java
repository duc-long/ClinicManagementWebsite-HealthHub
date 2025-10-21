package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.UserDTO;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.repository.TechnicianRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TechnicianService {
    TechnicianRepository technicianRepository;

    public TechnicianService(TechnicianRepository technicianRepository) {
        this.technicianRepository = technicianRepository;
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

    public User findByUserId(Integer userId) {
        return technicianRepository.findById(userId).get();
    }

    @Transactional
    public void save(User user) {
        technicianRepository.save(user);
    }
}
