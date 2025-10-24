
package com.group4.clinicmanagement.dto;

import com.group4.clinicmanagement.entity.Doctor;
import com.group4.clinicmanagement.entity.Patient;
import com.group4.clinicmanagement.enums.Gender;
import lombok.Data;

@Data
public class UserDTO {
    private String fullName;
    private String email;
    private String phone;
    private Gender gender;
    private int id;
    private String username;
    private Doctor doctor;
    private Patient patient;
    private String avatarFilename;

    public UserDTO() {
    }

    public UserDTO(String fullName, String email, String phone, Gender gender, int id) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.id = id;
    }

    public UserDTO(String fullName, String email, String phone, Gender gender, int id, Doctor doctor, Patient patient) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.id = id;
    }

    public UserDTO(String fullName, String email, String phone, Gender gender, int id, Patient patient) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.id = id;
    }
}
