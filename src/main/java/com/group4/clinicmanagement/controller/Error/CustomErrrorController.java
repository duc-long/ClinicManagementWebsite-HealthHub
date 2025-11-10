package com.group4.clinicmanagement.controller.Error;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CustomErrrorController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int code = 500;
        if (statusCode != null) {
            code = Integer.parseInt(statusCode.toString());
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Map role -> redirect page
        Map<String, String> roleRedirect = new HashMap<>();
        roleRedirect.put("ROLE_ADMIN", "/admin/dashboard");
        roleRedirect.put("ROLE_RECEPTIONIST", "/receptionist/appointment-list");
        roleRedirect.put("ROLE_CASHIER", "/cashier/appointment-list");
        roleRedirect.put("ROLE_DOCTOR", "/doctor/home");
        roleRedirect.put("ROLE_TECHNICIAN", "/technician/request-list");
        roleRedirect.put("ROLE_PATIENT", "/home");

        String redirectBase = "/error/" + code; // default page

        // Nếu đã đăng nhập thì điều hướng theo role
        if (auth != null && auth.isAuthenticated()) {
            Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
            for (GrantedAuthority r : roles) {
                if (roleRedirect.containsKey(r.getAuthority())) {
                    redirectBase = roleRedirect.get(r.getAuthority()) + "?error=" + code;
                    break;
                }
            }
        }

        // Nếu là lỗi phổ biến, render trang lỗi
        if (code == 403 || code == 404 || code == 405 || code == 500) {
            request.setAttribute("errorCode", code);
            return "error/" + code;
        }

        // fallback
        return "redirect:" + redirectBase;
    }

    public String getErrorPath() {
        return "/error";
    }
}

