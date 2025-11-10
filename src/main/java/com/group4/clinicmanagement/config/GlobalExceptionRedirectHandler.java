package com.group4.clinicmanagement.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

@Component
public class GlobalExceptionRedirectHandler extends SimpleMappingExceptionResolver {
}
