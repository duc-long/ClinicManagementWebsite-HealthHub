package com.group4.clinicmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Department")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Integer departmentId; // Mã khoa

    @Column(nullable = false, length = 100)
    private String name; // Tên khoa (vd: Khoa Nội, Khoa Nhi, ...)

    @Column(length = 500)
    private String description; // Mô tả chi tiết khoa

    // ========== Quan hệ 1-n với Doctor ==========
    // Một khoa có nhiều bác sĩ
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<Doctor> doctors;
}
