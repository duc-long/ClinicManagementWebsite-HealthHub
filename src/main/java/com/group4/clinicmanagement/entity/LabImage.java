package com.group4.clinicmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "lablmage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    private LabResult labResult;

    @Column(name = "file_path", nullable = false, length = 1000)
    private String filePath;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}