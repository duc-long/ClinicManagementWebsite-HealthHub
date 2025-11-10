package com.group4.clinicmanagement.exception.doctor;

import com.group4.clinicmanagement.controller.doctor.DoctorController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(assignableTypes = DoctorController.class)
public class DoctorExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(DoctorExceptionHandler.class);

    // Wrong ID
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException ex, RedirectAttributes redirectAttributes) {
        logger.warn("DoctorController: Invalid ID format -> {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("messageType", "error");
        redirectAttributes.addFlashAttribute("message", "Invalid ID format!");
        return "redirect:/doctor/home";
    }

    // HTTP (GET/POST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, RedirectAttributes redirectAttributes) {
        logger.warn("DoctorController: Method not allowed -> {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("messageType", "error");
        redirectAttributes.addFlashAttribute("message", "HTTP method not allowed for this URL (405).");
        return "redirect:/doctor/home";
    }

    // (404)
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(NoHandlerFoundException ex, RedirectAttributes redirectAttributes) {
        logger.warn("DoctorController: Page not found (404) -> {}", ex.getRequestURL());
        redirectAttributes.addFlashAttribute("messageType", "error");
        redirectAttributes.addFlashAttribute("message", "Requested page not found (404).");
        return "redirect:/doctor/home";
    }

    // (500)
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntime(RuntimeException ex, RedirectAttributes redirectAttributes) {
        logger.error("DoctorController: Runtime exception -> {}", ex.getMessage(), ex);
        redirectAttributes.addFlashAttribute("messageType", "error");
        redirectAttributes.addFlashAttribute("message", "Internal server error (500).");
        return "redirect:/doctor/home";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex, RedirectAttributes redirectAttributes) {
        logger.error("DoctorController: Unexpected exception -> {}", ex.getMessage(), ex);
        redirectAttributes.addFlashAttribute("messageType", "error");
        redirectAttributes.addFlashAttribute("message", "An unexpected error occurred.");
        return "redirect:/doctor/home";
    }
}
