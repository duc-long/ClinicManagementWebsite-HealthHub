package com.group4.clinicmanagement.config;

import com.group4.clinicmanagement.service.CustomUserDetailsService;
import com.group4.clinicmanagement.util.RoleMismatchLogoutFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomLoginSuccessHandler loginSuccessHandler;
    private final CustomLoginFailureHandler customLoginFailureHandler;
    private final RoleMismatchLogoutFilter roleMismatchLogoutFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService, CustomLoginSuccessHandler loginSuccessHandler, CustomLoginFailureHandler customLoginFailureHandler, RoleMismatchLogoutFilter roleMismatchLogoutFilter) {
        this.userDetailsService = userDetailsService;
        this.loginSuccessHandler = loginSuccessHandler;
        this.customLoginFailureHandler = customLoginFailureHandler;
        this.roleMismatchLogoutFilter = roleMismatchLogoutFilter;
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
                .addFilterBefore(roleMismatchLogoutFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/home/**", "/register/**", "/assets/**", "/images/**", "/auth/**").permitAll()
                        .requestMatchers("/patient/login").permitAll()
                        .requestMatchers("/patient/**", "/feedback/**").hasAuthority("ROLE_PATIENT")
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
                .csrf(Customizer.withDefaults());
        return http.build();
    }


    // ====================== DOCTOR ======================
    @Bean
    @Order(2)
    public SecurityFilterChain doctorChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/doctor/login","/doctor/**")
                .addFilterBefore(roleMismatchLogoutFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/doctor/login", "/assets/**", "/images/**", "/css/**","/js/**").permitAll()
                        .anyRequest().hasAuthority("ROLE_DOCTOR"))
                .formLogin(form -> form
                        .loginPage("/doctor/login")
                        .loginProcessingUrl("/doctor/login")
                        .failureHandler(customLoginFailureHandler)
                        .successHandler(loginSuccessHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/doctor/logout")
                        .logoutSuccessUrl("/doctor/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .clearAuthentication(true)
                        .permitAll())
                .csrf(Customizer.withDefaults());
        return http.build();
    }

    // ====================== ADMIN ======================
    @Bean
    @Order(3)
    public SecurityFilterChain adminChain(HttpSecurity http) throws Exception {
        http.securityMatcher( "/admin/login","/admin/**")
                .addFilterBefore(roleMismatchLogoutFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login", "/assets/**").permitAll()
                        .anyRequest().hasAuthority("ROLE_ADMIN"))
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
                .csrf(Customizer.withDefaults());
        return http.build();
    }

    // ====================== RECEPTIONIST ======================
    @Bean
    @Order(4)
    public SecurityFilterChain receptionistChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/receptionist/login","/receptionist/**")
                .addFilterBefore(roleMismatchLogoutFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/receptionist/login", "/assets/**").permitAll()
                        .anyRequest().hasAuthority("ROLE_RECEPTIONIST"))
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
                .csrf(Customizer.withDefaults());
        return http.build();
    }

    // ====================== CASHIER ======================
    @Bean
    @Order(5)
    public SecurityFilterChain cashierChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/cashier/login","/cashier/**")
                .addFilterBefore(roleMismatchLogoutFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/cashier/login", "/assets/**").permitAll()
                        .anyRequest().hasAuthority("ROLE_CASHIER"))
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
                .csrf(Customizer.withDefaults());
        return http.build();
    }

    // ====================== TECHNICIAN ======================
    @Bean
    @Order(6)
    public SecurityFilterChain technicianChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/technician/login","/technician/**")
                .addFilterBefore(roleMismatchLogoutFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/technician/login", "/assets/**").permitAll()
                        .anyRequest().hasAuthority("ROLE_TECHNICIAN"))
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
                .csrf(Customizer.withDefaults());
        return http.build();
    }
}
