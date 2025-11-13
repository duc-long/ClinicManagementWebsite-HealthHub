package com.group4.clinicmanagement.service;

import com.group4.clinicmanagement.entity.Staff;
import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.enums.UserStatus;
import com.group4.clinicmanagement.repository.StaffRepository;
import com.group4.clinicmanagement.repository.PatientRepository;
import com.group4.clinicmanagement.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final StaffRepository staffRepository;
    private final PatientRepository patientRepository;
    private final HttpServletRequest request;

    public CustomUserDetailsService(StaffRepository staffRepository, PatientRepository patientRepository, HttpServletRequest request) {
        this.staffRepository = staffRepository;
        this.patientRepository = patientRepository;
        this.request = request;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String uri = request.getRequestURI();

        // Kiểm tra xem là Staff hay Patient và lấy thông tin từ repository tương ứng
        Staff staffUser = staffRepository.findStaffByUsername(username).orElse(null);
        Patient patientUser = patientRepository.findPatientByUsername(username).orElse(null);

        // Nếu cả Staff và Patient đều không tồn tại
        if (staffUser == null && patientUser == null) {
            throw new UsernameNotFoundException("Account does not exist.");
        }

        // Kiểm tra trạng thái của tài khoản
        if (staffUser != null && staffUser.getStatus() != UserStatus.ACTIVE) {
            throw new DisabledException("Staff account is locked or not activated.");
        }
        if (patientUser != null && patientUser.getStatus() != UserStatus.ACTIVE) {
            throw new DisabledException("Patient account is locked or not activated.");
        }

        // Kiểm tra quyền truy cập vào các trang
        if (uri.startsWith("/doctor") && (staffUser == null || !staffUser.getRole().getName().equalsIgnoreCase("Doctor"))) {
            throw new AuthenticationServiceException("You do not have permission to log in to the Doctor page.");
        }
        if (uri.startsWith("/patient") && (patientUser == null)) {
            throw new AuthenticationServiceException("You do not have permission to log in to the Patient page.");
        }
        if (uri.startsWith("/admin") && (staffUser == null || !staffUser.getRole().getName().equalsIgnoreCase("Admin"))) {
            throw new AuthenticationServiceException("You do not have permission to log in to the Admin page.");
        }
        if (uri.startsWith("/technician") && (staffUser == null || !staffUser.getRole().getName().equalsIgnoreCase("Technician"))) {
            throw new AuthenticationServiceException("You do not have permission to log in to the Technician page.");
        }

        // Trả về đối tượng CustomUserDetails tương ứng với Staff hoặc Patient
        if (staffUser != null) {
            return new CustomUserDetails(staffUser);  // Trả về CustomUserDetails cho Staff
        } else if (patientUser != null) {
            return new CustomUserDetails(patientUser);  // Trả về CustomUserDetails cho Patient
        }

        throw new UsernameNotFoundException("Account does not exist.");
    }

}
