package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.repository.ReceptionistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReceptionistService {
   private final ReceptionistRepository receptionistRepository;

    public ReceptionistService(ReceptionistRepository receptionistRepository) {
        this.receptionistRepository = receptionistRepository;
    }

    @Transactional
    public User findUserById(int id){
        return receptionistRepository.getReferenceById(id);
    }

    public void save(User user){
        receptionistRepository.save(user);
    }
}
