package com.group4.clinicmanagement.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RoleMismatchLogoutFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String uri = request.getRequestURI();
            String role = auth.getAuthorities().iterator().next().getAuthority();

            // Xác định role hợp lệ dựa theo đường dẫn
            String expectedRole = null;
            if (uri.startsWith("/cashier")) expectedRole = "ROLE_CASHIER";
            else if (uri.startsWith("/technician")) expectedRole = "ROLE_TECHNICIAN";
            else if (uri.startsWith("/doctor")) expectedRole = "ROLE_DOCTOR";
            else if (uri.startsWith("/admin")) expectedRole = "ROLE_ADMIN";
            else if (uri.startsWith("/receptionist")) expectedRole = "ROLE_RECEPTIONIST";
            else if (uri.startsWith("/patient")) expectedRole = "ROLE_PATIENT";

            // Nếu đang login bằng role khác → tự động logout và redirect
            if (expectedRole != null && !role.equals(expectedRole)) {

                // Thực hiện logout
                new SecurityContextLogoutHandler().logout(request, response, auth);

                // Xóa toàn bộ thông tin xác thực trong thread hiện tại
                SecurityContextHolder.clearContext();

                // Redirect đến trang login tương ứng, kèm query param báo auto logout
                response.sendRedirect(expectedRoleToLoginPage(expectedRole) + "?autoLogout=true");

                // Dừng filter chain, không cho Spring Security tiếp tục xử lý
                return;
            }
        }

        // Nếu không vi phạm role thì tiếp tục chain
        filterChain.doFilter(request, response);
    }

    private String expectedRoleToLoginPage(String role) {
        switch (role) {
            case "ROLE_CASHIER": return "/cashier/login";
            case "ROLE_TECHNICIAN": return "/technician/login";
            case "ROLE_DOCTOR": return "/doctor/login";
            case "ROLE_ADMIN": return "/admin/login";
            case "ROLE_RECEPTIONIST": return "/receptionist/login";
            case "ROLE_PATIENT": return "/patient/login";
            default: return "/home";
        }
    }
}
