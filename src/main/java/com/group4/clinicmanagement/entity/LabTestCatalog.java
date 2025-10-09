package com.group4.clinicmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "LabTestCatalog")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabTestCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_id")
    private Integer testId;

    @Column(nullable = false, length = 200)
    private String name; // Tên xét nghiệm

    @Column(length = 1000)
    private String description; // Mô tả chi tiết

    private Double cost; // Chi phí xét nghiệm
    private Integer status; // 0=inactive, 1=active
    private LocalDateTime createdAt;

    // Một loại xét nghiệm có thể được dùng trong nhiều yêu cầu
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LabRequest> labRequests;
}