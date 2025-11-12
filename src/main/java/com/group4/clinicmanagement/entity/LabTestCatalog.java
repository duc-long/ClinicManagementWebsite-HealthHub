package com.group4.clinicmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(
        name = "LabTestCatalog",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "labRequests") // Tránh LazyInitializationException khi log
public class LabTestCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_id")
    private Integer testId;

    // ========================================
    // 1. Tên xét nghiệm – BẮT BUỘC, DUY NHẤT
    // ========================================
    @Column(name = "name", nullable = false, length = 200, unique = true)
    private String name;

    // ========================================
    // 2. Mô tả
    // ========================================
    @Column(name = "description", length = 1000)
    private String description;

    // ========================================
    // 3. Chi phí – BẮT BUỘC
    // ========================================
    @Column(name = "cost", nullable = false)
    private Double cost;

    // ========================================
    // 4. Trạng thái (1 = active, 0 = inactive)
    // ========================================
    @Column(name = "status", nullable = false)
    private Integer status = 1;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(
            mappedBy = "test",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
            orphanRemoval = false, // Không xóa test khi xóa request
            fetch = FetchType.LAZY
    )
    private List<LabRequest> labRequests = new ArrayList<>();
}