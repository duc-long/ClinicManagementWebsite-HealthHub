package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public boolean isUsernameDuplicate(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

}
