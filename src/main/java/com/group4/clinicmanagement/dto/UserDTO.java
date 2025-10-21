
package com.group4.clinicmanagement.dto;

import com.group4.clinicmanagement.enums.Gender;

public class UserDTO {
    private String fullName;
    private String email;
    private String phone;
    private Gender gender;
    private int id;

    public UserDTO() {
    }

    public UserDTO(String fullName, String email, String phone, Gender gender, int id) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
