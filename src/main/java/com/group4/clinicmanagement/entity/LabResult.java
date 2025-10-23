package com.group4.clinicmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "labresult")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer resultId;

    @ManyToOne
    @JoinColumn(name = "lab_request_id", nullable = false)
    private LabRequest labRequest;

    @ManyToOne
    @JoinColumn(name = "technician_id", nullable = false)
    private User technician;

    @Column(columnDefinition = "TEXT")
    private String resultText;

    @OneToMany(mappedBy="labResult")
    private List<LabImage> images;

    private LocalDateTime createdAt;
}
