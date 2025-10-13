package com.group4.clinicmanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cấu hình phục vụ ảnh trong thư mục avatars
        registry.addResourceHandler("/images/avatars/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/avatars/");

        // Cấu hình phục vụ ảnh trong thư mục images
        registry.addResourceHandler("/images/labs/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/images/labs/");
    }
}
