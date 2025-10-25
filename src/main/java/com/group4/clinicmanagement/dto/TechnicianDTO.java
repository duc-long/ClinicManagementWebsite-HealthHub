package com.group4.clinicmanagement.dto;

import com.group4.clinicmanagement.entity.User;
import com.group4.clinicmanagement.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class TechnicianDTO {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must be less than 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Invalid phone number format")
    private String phone;

    private Gender gender;
    private int id;
    private String username;
    private String avatarFilename;

    // Constructor
    public TechnicianDTO() {}

    public TechnicianDTO(String fullName, String email, String phone, Gender gender,
                         int id, String username, String avatar) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.id = id;
        this.username = username;
        this.avatarFilename = avatar;
    }

    // Mapping từ Entity -> DTO
    public static TechnicianDTO fromEntity(User user) {
        if (user == null) return null;
        TechnicianDTO dto = new TechnicianDTO();
        dto.setId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setGender(user.getGender());
        dto.setUsername(user.getUsername());
        dto.setAvatarFilename(user.getAvatar());
        return dto;
    }
}
