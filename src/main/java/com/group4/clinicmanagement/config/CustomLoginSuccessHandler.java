package com.group4.clinicmanagement.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String requestURI = request.getRequestURI(); // login page URL
        Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();

        String redirectURL = "/home"; // default fallback

        // ==================== RECEPTIONIST ====================
        if(requestURI.startsWith("/receptionist/login")) {
            if(hasRole(roles, "ROLE_Receptionist")) {
                redirectURL = "/receptionist/appointment-list";
            } else {
                // tài khoản không hợp lệ cho trang này
                response.sendRedirect("/receptionist/login?error=invalid_role");
                return;
            }
        }
        // ==================== CASHIER ====================
        else if(requestURI.startsWith("/cashier/login")) {
            if(hasRole(roles, "ROLE_Cashier")) {
                redirectURL = "/cashier/appointment-list";
            } else {
                response.sendRedirect("/cashier/login?error=invalid_role");
                return;
            }
        }
        // ==================== DOCTOR ====================
        else if(requestURI.startsWith("/doctor/login")) {
            if(hasRole(roles, "ROLE_Doctor")) {
                redirectURL = "/doctor/home";
            } else {
                response.sendRedirect("/doctor/login?error=invalid_role");
                return;
            }
        }
        // ==================== ADMIN ====================
        else if(requestURI.startsWith("/admin/login")) {
            if(hasRole(roles, "ROLE_Admin")) {
                redirectURL = "/admin/dashboard";
            } else {
                response.sendRedirect("/admin/login?error=invalid_role");
                return;
            }
        }
        // ==================== TECHNICIAN ====================
        else if(requestURI.startsWith("/technician/login")) {
            if(hasRole(roles, "ROLE_Technician")) {
                redirectURL = "/technician/dashboard";
            } else {
                response.sendRedirect("/technician/login?error=invalid_role");
                return;
            }
        }
        // ==================== PATIENT ====================
        else if(requestURI.startsWith("/patient/login")) {
            if(hasRole(roles, "ROLE_Patient")) {
                redirectURL = "/home";
            } else {
                response.sendRedirect("/patient/login?error=invalid_role");
                return;
            }
        }
        // ==================== fallback ====================
        else {
            redirectURL = "/home";
        }

        response.sendRedirect(redirectURL);
    }

    // helper method check role
    private boolean hasRole(Collection<? extends GrantedAuthority> roles, String roleName) {
        return roles.stream().anyMatch(r -> r.getAuthority().equals(roleName));
    }
}
