package com.group4.clinicmanagement.config;

import com.group4.clinicmanagement.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final CustomLoginSuccessHandler loginSuccessHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService,  CustomLoginSuccessHandler loginSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Cấu hình rule bảo mật
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/home/**", "/login/**", "/register/**",
                                "/css/**", "/js/**", "/assets/**", "/images/**", "/error").permitAll()
                        .requestMatchers("/doctor/**").hasRole("Doctor") // page doctor
                        .requestMatchers("/patient/**").hasRole("Patient") // page patient
                        .requestMatchers("/admin/**").hasRole("Admin") // page admin
                        .requestMatchers("/receptionist/**").hasRole("Receptionist") // page receptionist
                        .requestMatchers("/cashier/**").hasRole("Cashier") // page cashier
                        .anyRequest().authenticated()
                )
                // form login for patient
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(loginSuccessHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")  // URL logout
                        .logoutSuccessUrl("/login?logout") // redirect after logout
                        .invalidateHttpSession(true) // delete session
                        .deleteCookies("JSESSIONID") // delete cookies
                        .permitAll()
                )
                .requestCache(requestCache -> requestCache.disable()); // ✅ tránh loop

        return http.build();
    }
}
