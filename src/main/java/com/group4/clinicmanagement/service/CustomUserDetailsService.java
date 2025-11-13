package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.Staff;
import com.group4.clinicmanagement.enums.UserStatus;
import com.group4.clinicmanagement.repository.StaffRepository;
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
    private final StaffRepository staffRepository;
    private HttpServletRequest request;

    public CustomUserDetailsService(StaffRepository staffRepository, HttpServletRequest request) {
        this.staffRepository = staffRepository;
        this.request = request;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String uri = request.getRequestURI();

        Staff user = staffRepository.findStaffByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Account does not exist."));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new DisabledException("Account is locked or not activated.");
        }

        String role = user.getRole().getName();

        // Kiểm tra đăng nhập đúng vai trò
        if (uri.startsWith("/doctor") && !role.equalsIgnoreCase("Doctor")) {
            throw new AuthenticationServiceException("You do not have permission to log in to the Doctor page.");
        }
        if (uri.startsWith("/patient") && !role.equalsIgnoreCase("Patient")) {
            throw new AuthenticationServiceException("You do not have permission to log in to the Patient page.");
        }
        if (uri.startsWith("/admin") && !role.equalsIgnoreCase("Admin")) {
            throw new AuthenticationServiceException("You do not have permission to log in to the Admin page.");
        }
        if (uri.startsWith("/technician") && !role.equalsIgnoreCase("Technician")) {
            throw new AuthenticationServiceException("You do not have permission to log in to the Technician page.");
        }

        return new CustomUserDetails(user);
    }
}
