//package com.group4.clinicmanagement.controller.Error;
//
//import jakarta.servlet.RequestDispatcher;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.boot.web.servlet.error.ErrorController;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Controller
//public class CustomErrrorController implements ErrorController {
//    @RequestMapping("/error")
//    public String handleError(HttpServletRequest request, Model model) {
//
//        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
//        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
//        Object uri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
//
//        int code = 500;
//        if (status != null) {
//            try {
//                code = Integer.parseInt(status.toString());
//            } catch (NumberFormatException ignored) {}
//        }
//
//        String errorMsg = (message != null) ? message.toString() : "An unexpected error occurred";
//        String errorUri = (uri != null) ? uri.toString() : "N/A";
//
//
//        model.addAttribute("errorCode", code);
//        model.addAttribute("errorMessage", errorMsg);
//        model.addAttribute("errorPath", errorUri);
//
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//        Map<String, String> roleRedirect = new HashMap<>();
//        roleRedirect.put("ROLE_ADMIN", "/admin/dashboard");
//        roleRedirect.put("ROLE_RECEPTIONIST", "/receptionist/appointment-list");
//        roleRedirect.put("ROLE_CASHIER", "/cashier/appointment-list");
//        roleRedirect.put("ROLE_DOCTOR", "/doctor/home");
//        roleRedirect.put("ROLE_TECHNICIAN", "/technician/request-list");
//        roleRedirect.put("ROLE_PATIENT", "/home");
//
//        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
//            for (GrantedAuthority r : auth.getAuthorities()) {
//                if (roleRedirect.containsKey(r.getAuthority())) {
//                    model.addAttribute("backLink", roleRedirect.get(r.getAuthority()));
//                    break;
//                }
//            }
//        } else {
//            model.addAttribute("backLink", "/login");
//        }
//
//        switch (code) {
//            case 403:
//                model.addAttribute("errorTitle", "Access Denied");
//                return "error/403";
//            case 500:
//                model.addAttribute("errorTitle", "Internal Server Error");
//                return "error/500";
//            case 405:
//                model.addAttribute("errorTitle", "Method Not Allowed");
//                return "error/405";
//            default:
//                model.addAttribute("errorTitle", "Page Not Found");
//                return "error/404";
//        }
//    }
//
//    public String getErrorPath() {
//        return "/error";
//    }
//}
//
