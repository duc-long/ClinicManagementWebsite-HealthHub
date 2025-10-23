package com.group4.clinicmanagement.dto;

import com.group4.clinicmanagement.enums.Gender;
import com.group4.clinicmanagement.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceptionistUserDTO {
    private Integer userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private Gender gender;
    private String avatarFilename;
    private UserStatus userStatus;
}
