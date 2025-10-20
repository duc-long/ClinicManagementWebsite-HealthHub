package com.group4.clinicmanagement;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

public class BcryptGenerator {
    public static void main(String[] args) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "123";
        String hashedPassword = encoder.encode(rawPassword);

        System.out.println("Generated hash: " + hashedPassword);
        System.out.println("Verify again: " + encoder.matches(rawPassword, hashedPassword));

//        String raw = "123";
//        String hash = "$2a$10$Or2SCfwiuycrSAPyUODcNOI82tCWFPttYlF4fFBbmVKp15vhG/L2W";

        BCryptPasswordEncoder encoder2 = new BCryptPasswordEncoder();
        boolean result = encoder2.matches("123", "$2a$10$Or2SCfwiuycrSAPyUODcNOI82tCWFPttYlF4fFBbmVKp15vhG/L2W");
        System.out.println("✅ matches: " + result);
    }
}
