package com.group4.clinicmanagement.dto;

import com.group4.clinicmanagement.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorUserDTO {

    private Integer doctorId;

    // Thông tin từ User
    private String fullName;
    private String email;
    private String phone;
    private Gender gender;

    // Thông tin chuyên môn từ Doctor
    private String licenseNo;
    private String specialty;
    private String degree;
    private Integer yearsExperience;
    private String bio;
    private boolean profileVisibility;

    // Tên khoa (nếu bạn muốn show lên UI)
    private String departmentName;
}
