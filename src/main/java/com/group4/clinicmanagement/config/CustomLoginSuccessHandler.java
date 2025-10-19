package com.group4.clinicmanagement.config;

import jakarta.servlet.FilterChain;
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
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String redirectURL = request.getParameter("redirectURL"); // default url

        // get role list from user after login
        Collection<?extends GrantedAuthority> grantedAuthorities = authentication.getAuthorities();

        for(GrantedAuthority grantedAuthority : grantedAuthorities){
            String role = grantedAuthority.getAuthority(); // get Role

            // check role
            switch (role){
                case "ROLE_Admin":
                case "ROLE_Doctor":
                    redirectURL = "/doctor/overview";
                    break;
                case "ROLE_Patient":
                case "ROLE_Receptionist":
                case "ROLE_Cashier":
                case "ROLE_Technician":
                default:
                    redirectURL = "/home"; // fallback
            }

            // redirect to select URL
            response.sendRedirect(redirectURL);
        }
    }
}
