package com.group4.clinicmanagement.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String uri = request.getRequestURI();
        String redirect;

        if (uri.contains("/doctor")) {
            redirect = "/doctor/login";
        } else if (uri.contains("/admin")) {
            redirect = "/admin/login";
        } else if (uri.contains("/receptionist")) {
            redirect = "/receptionist/login";
        } else if (uri.contains("/cashier")) {
            redirect = "/cashier/login";
        } else if (uri.contains("/technician")) {
            redirect = "/technician/login";
        } else {
            redirect = "/patient/login";
        }

        // Lấy message gốc từ exception
        String message = exception.getMessage();

        // Nếu Spring trả "Bad credentials" → đổi thành thân thiện hơn
        if ("Bad credentials".equalsIgnoreCase(message)) {
            message = "Tên đăng nhập hoặc mật khẩu không chính xác.";
        }

        // Lưu message vào session để hiển thị lên form
        request.getSession().setAttribute("LOGIN_ERROR_MESSAGE", message);

        response.sendRedirect(redirect + "?error");
    }
}
