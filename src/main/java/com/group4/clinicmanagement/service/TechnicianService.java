package com.group4.clinicmanagement.service;

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
    public User findByUserId(Integer userId) {
        return technicianRepository.findById(userId).get();
    }
    @Transactional
    public void save(User user) {
        technicianRepository.save(user);
    }
}
