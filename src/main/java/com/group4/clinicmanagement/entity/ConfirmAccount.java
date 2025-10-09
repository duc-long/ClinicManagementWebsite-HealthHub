package com.group4.clinicmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ConfirmAccount")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "confirm_id")
    private Integer confirmId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Liên kết tới tài khoản người dùng

    @Column(name = "confirm_token", nullable = false, unique = true, length = 255)
    private String confirmToken; // Mã xác nhận (token hoặc code)

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt; // Hết hạn xác nhận

    private LocalDateTime confirmedAt; // Thời điểm xác nhận
    private Boolean isConfirmed = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
