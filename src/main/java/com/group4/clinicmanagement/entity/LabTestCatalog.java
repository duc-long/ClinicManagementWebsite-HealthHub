package com.group4.clinicmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "labtestcatalog")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "labRequests")
public class LabTestCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_id")
    private Integer testId;

    @Column(name = "name", nullable = false, length = 200, unique = true)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "cost", nullable = false)
    private Double cost;

    @Column(name = "status", nullable = false)
    private Integer status = 1;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(
            mappedBy = "test",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
            orphanRemoval = false,
            fetch = FetchType.LAZY
    )
    private List<LabRequest> labRequests = new ArrayList<>();
}