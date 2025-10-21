package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.enums.UserStatus;
import com.group4.clinicmanagement.repository.UserRepository;
import com.group4.clinicmanagement.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private HttpServletRequest request;

    public CustomUserDetailsService(UserRepository userRepository, HttpServletRequest request) {
        this.userRepository = userRepository;
        this.request = request;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String uri = request.getRequestURI();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new DisabledException("Tài khoản bị khóa hoặc chưa kích hoạt");
        }

        String role = user.getRole().getName();

        // Kiểm tra đăng nhập đúng vai trò
        if (uri.startsWith("/doctor") && !role.equalsIgnoreCase("Doctor")) {
            throw new AuthenticationServiceException("Bạn không có quyền đăng nhập vào trang Doctor");
        }
        if (uri.startsWith("/patient") && !role.equalsIgnoreCase("Patient")) {
            throw new AuthenticationServiceException("Bạn không có quyền đăng nhập vào trang Patient");
        }
        if (uri.startsWith("/admin") && !role.equalsIgnoreCase("Admin")) {
            throw new AuthenticationServiceException("Bạn không có quyền đăng nhập vào trang Admin");
        }

        return new CustomUserDetails(user);
    }
}
