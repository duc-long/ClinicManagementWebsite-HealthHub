package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.repository.UserRepository;
import com.group4.clinicmanagement.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("❌ Không tìm thấy user: " + username);
                    return new UsernameNotFoundException("Username not found");
                });

        // Debug thông tin user
        System.out.println("✅ Tìm thấy user: " + user.getUsername());
        System.out.println("🔑 Password Hash: " + user.getPasswordHash());
        return new CustomUserDetails(user);
    }
}
