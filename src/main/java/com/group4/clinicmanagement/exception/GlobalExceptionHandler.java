package com.group4.clinicmanagement.exception;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {
    // 🧩 Bắt lỗi 405 Method Not Allowed
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex,
                                         RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error",
                "Invalid action: This operation is not allowed via direct URL.");
        return "redirect:/home";
    }

    // 🧩 Bắt lỗi chung khác (ví dụ NullPointer, IllegalState...)
    @ExceptionHandler(Exception.class)
    public String handleGeneralError(Exception ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "An unexpected error occurred. Please try again.");
        return "redirect:/home";
    }
}
