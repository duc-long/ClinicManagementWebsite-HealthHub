package com.group4.clinicmanagement.config;

import com.group4.clinicmanagement.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomLoginSuccessHandler loginSuccessHandler;
    private final CustomLoginFailureHandler customLoginFailureHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          CustomLoginSuccessHandler loginSuccessHandler,
                          CustomLoginFailureHandler customLoginFailureHandler) {
        this.userDetailsService = userDetailsService;
        this.loginSuccessHandler = loginSuccessHandler;
        this.customLoginFailureHandler = customLoginFailureHandler;
    }

    // ====================== COMMON BEANS ======================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ====================== GUEST & PATIENT ======================
    @Bean
    @Order(1)
    public SecurityFilterChain guestAndPatientChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/patient/login", "/patient/**", "/home/**", "/register/**", "/assets/**", "/images/**", "/feedback/**", "/auth/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/home/**", "/register/**", "/assets/**", "/images/**", "/auth/**").permitAll()
                        .requestMatchers("/patient/login").permitAll()
                        .requestMatchers("/patient/**", "/feedback/**").hasRole("Patient")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/patient/login")
                        .loginProcessingUrl("/patient/login")
                        .failureHandler(customLoginFailureHandler)
                        .successHandler(loginSuccessHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/patient/logout")
                        .logoutSuccessUrl("/home?logout")
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable());
        return http.build();
    }


    // ====================== DOCTOR ======================
    @Bean
    @Order(2)
    public SecurityFilterChain doctorChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/doctor/**", "/doctor/login")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/doctor/login", "/assets/**", "/images/**", "/css/**","/js/**").permitAll()
                        .anyRequest().hasRole("Doctor"))
                .formLogin(form -> form
                        .loginPage("/doctor/login")
                        .loginProcessingUrl("/doctor/login")
                        .failureHandler(customLoginFailureHandler)
                        .successHandler(loginSuccessHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/doctor/logout")
                        .logoutSuccessUrl("/doctor/login?logout")
                        .permitAll())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    // ====================== ADMIN ======================
    @Bean
    @Order(3)
    public SecurityFilterChain adminChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/admin/**", "/admin/login")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login", "/assets/**").permitAll()
                        .anyRequest().permitAll())
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .failureHandler(customLoginFailureHandler)
                        .successHandler(loginSuccessHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login?logout")
                        .permitAll())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    // ====================== RECEPTIONIST ======================
    @Bean
    @Order(4)
    public SecurityFilterChain receptionistChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/receptionist/**", "/receptionist/login")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/receptionist/login", "/assets/**").permitAll()
                        .anyRequest().hasRole("Receptionist"))
                .formLogin(form -> form
                        .loginPage("/receptionist/login")
                        .loginProcessingUrl("/receptionist/login")
                        .failureHandler(customLoginFailureHandler)
                        .successHandler(loginSuccessHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/receptionist/logout")
                        .logoutSuccessUrl("/receptionist/login?logout")
                        .permitAll())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    // ====================== CASHIER ======================
    @Bean
    @Order(5)
    public SecurityFilterChain cashierChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/cashier/**", "/cashier/login")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/cashier/login", "/assets/**").permitAll()
                        .anyRequest().hasRole("Cashier"))
                .formLogin(form -> form
                        .loginPage("/cashier/login")
                        .loginProcessingUrl("/cashier/login")
                        .failureHandler(customLoginFailureHandler)
                        .successHandler(loginSuccessHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/cashier/logout")
                        .logoutSuccessUrl("/cashier/login?logout")
                        .permitAll())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    // ====================== TECHNICIAN ======================
    @Bean
    @Order(6)
    public SecurityFilterChain technicianChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/technician/**", "/technician/login")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/technician/login", "/assets/**").permitAll()
                        .anyRequest().hasRole("Technician"))
                .formLogin(form -> form
                        .loginPage("/technician/login")
                        .loginProcessingUrl("/technician/login")
                        .failureHandler(customLoginFailureHandler)
                        .successHandler(loginSuccessHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/technician/logout")
                        .logoutSuccessUrl("/technician/login?logout")
                        .permitAll())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }
}
