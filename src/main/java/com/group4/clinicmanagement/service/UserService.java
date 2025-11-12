package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.DoctorDTO;
import com.group4.clinicmanagement.dto.UserDTO;
import com.group4.clinicmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

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

    // method to change password
    public boolean changePassword(User user, String currentPassword, String newPassword) {
        if (user == null || currentPassword == null || newPassword == null) {
            return false;
        }

        String currentHash = user.getPasswordHash();
        if (currentHash == null || !passwordEncoder.matches(currentPassword, currentHash)) {
            return false;
        }

        if (!isValidNewPassword(newPassword)) {
            return false;
        }

        String encoded = passwordEncoder.encode(newPassword);
        user.setPasswordHash(encoded);
        User saved = userRepository.save(user);
        return saved != null;
    }

    private boolean isValidNewPassword(String newPassword) {
        if (newPassword == null) return false;
        // Ít nhất 6 ký tự, chứa ít nhất 1 chữ, 1 số, 1 ký tự đặc biệt
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{6,}$";
        return newPassword.matches(regex);
    }

    public boolean isMailNoDuplicate(String mail, Integer id) {
        User user = userRepository.getUsersByUserId(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getEmail().equals(mail)) {
            return false;
        } else {
            return userRepository.findUserByEmail(mail).isPresent();
        }
    }

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
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
