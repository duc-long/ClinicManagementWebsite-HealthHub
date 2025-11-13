package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.dto.UserDTO;
import com.group4.clinicmanagement.entity.Staff;
import com.group4.clinicmanagement.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {
    private final StaffRepository staffRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public boolean isUsernameDuplicate(String username) {
        return staffRepository.findByUsername(username).isPresent();
    }

    public Staff findUserByUsername(String username) {
        Staff user = staffRepository.findStaffByUsername(username)
                .orElse(null);
        return user;
    }

    // method to get user DTO by username
    public UserDTO findUserDTOByUsername(String username) {
        Staff user = staffRepository.findStaffByUsername(username)
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
    public boolean changePassword(Staff user, String currentPassword, String newPassword) {
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
        Staff saved = staffRepository.save(user);
        return saved != null;
    }

    private boolean isValidNewPassword(String newPassword) {
        if (newPassword == null) return false;
        // Ít nhất 6 ký tự, chứa ít nhất 1 chữ, 1 số, 1 ký tự đặc biệt
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{6,}$";
        return newPassword.matches(regex);
    }

    public boolean isMailNoDuplicate(String mail, Integer id) {
        Staff user = staffRepository.findByStaffId(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getEmail().equals(mail)) {
            return false;
        } else {
            return staffRepository.findUserByEmail(mail).isPresent();
        }
    }

    @Transactional
    public Staff saveUser(Staff user) {
        return staffRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Staff user = staffRepository.findByUsernameIgnoreCase(username);
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
