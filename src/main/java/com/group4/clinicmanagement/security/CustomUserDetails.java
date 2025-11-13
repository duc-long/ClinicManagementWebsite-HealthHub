package com.group4.clinicmanagement.security;

import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.entity.Staff;
import com.group4.clinicmanagement.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Object user; // Staff hoặc Patient
    private final String username;
    private final String password;
    private final String role;

    // Constructor cho Staff
    public CustomUserDetails(Staff staff) {
        this.user = staff;
        this.username = staff.getUsername();
        this.password = staff.getPasswordHash();
        this.role = "ROLE_" + staff.getRole().getName(); // ROLE_DOCTOR, ROLE_ADMIN...
    }

    // Constructor cho Patient
    public CustomUserDetails(Patient patient) {
        this.user = patient;
        this.username = patient.getUsername();
        this.password = patient.getPasswordHash();
        this.role = "ROLE_PATIENT"; // Patient luôn có role này
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        if (user instanceof Staff staff) {
            return staff.getStatus().getValue() == 1; // hoặc Boolean
        } else if (user instanceof Patient patient) {
            return patient.getStatus() == UserStatus.ACTIVE; // hoặc Boolean
        }
        return false;
    }

    // === HELPER METHODS ===
    public boolean isStaff() {
        return user instanceof Staff;
    }

    public boolean isPatient() {
        return user instanceof Patient;
    }

    public Staff getStaff() {
        return isStaff() ? (Staff) user : null;
    }

    public Patient getPatient() {
        return isPatient() ? (Patient) user : null;
    }

    public Integer getUserId() {
        return isStaff() ? getStaff().getStaffId() : getPatient().getPatientId();
    }

    public String getFullName() {
        return isStaff() ? getStaff().getFullName() : getPatient().getFullName();
    }
}