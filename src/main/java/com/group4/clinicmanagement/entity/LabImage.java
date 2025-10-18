package com.group4.clinicmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import java.time.LocalDateTime;

@Entity
@Table(name = "Labimage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId;

    @ManyToOne
    @JoinColumn(name = "result_id", nullable = false)
    private LabResult labResult; // Liên kết tới kết quả xét nghiệm

    @Column(name = "file_path", nullable = false, length = 1000)
    private String filePath; // Đường dẫn ảnh

    @Column(length = 500)
    private String description; // Ghi chú

    private LocalDateTime createdAt;
}
