package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.UserDTO;
import com.group4.clinicmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isUsernameDuplicate(String username) {
        return userRepository.findByUsername(username).isPresent();
    }


    public User findUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElse(null);
        return user;
    }

    // method to get user DTO by username
    public UserDTO findUserDTOByUsername(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElse(null);

        if (user == null) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        user.setGender(user.getGender());
        user.setPhone(user.getPhone());

        return userDTO;
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameIgnoreCase(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username not found");
        }
        System.out.println(user.getRole().getName());
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities("ROLE_" + user.getRole().getName().toUpperCase())
                .build();
    }

}
