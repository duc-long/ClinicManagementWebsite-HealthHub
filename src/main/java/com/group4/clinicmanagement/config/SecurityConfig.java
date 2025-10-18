//package com.group4.clinicmanagement.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class SecurityConfig {
//    private final CustomUserDetailsService userDetailsService;
//
//    public SecurityConfig(CustomUserDetailsService userDetailsService) {
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
//                .antMatchers("/", "/login", "/register", "/home/**", "/css/**", "/js/**", "/images/**").permitAll()  // Các trang công khai
//                .antMatchers("/patient/**").hasRole("PATIENT")  // Yêu cầu đăng nhập cho các trang của patient
//                .anyRequest().authenticated()  // Các trang khác yêu cầu xác thực
//                .and()
//                .formLogin()
//                .loginPage("/login")  // Cấu hình trang login
//                .defaultSuccessUrl("/patient/profile", true)  // Sau khi login thành công, chuyển hướng tới profile
//                .permitAll()
//                .and()
//                .logout()
//                .logoutUrl("/logout")  // Cấu hình trang logout
//                .logoutSuccessUrl("/login?logout")  // Sau khi logout, chuyển về trang login
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID")
//                .permitAll();
//
//        return http.build();
//    }
//}
