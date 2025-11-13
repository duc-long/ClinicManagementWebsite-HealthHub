package com.group4.clinicmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "labresult")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"labRequest", "technician", "images"})
public class LabResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Integer resultId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_request_id", nullable = false, unique = true)
    private LabRequest labRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id", nullable = false)
    private Staff technician;

    @Column(name = "result_text", columnDefinition = "NVARCHAR(MAX)")
    private String resultText;

    @OneToMany(
            mappedBy = "labResult",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<LabImage> images = new ArrayList<>();

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}