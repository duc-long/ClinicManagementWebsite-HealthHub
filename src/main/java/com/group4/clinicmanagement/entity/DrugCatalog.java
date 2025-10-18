package com.group4.clinicmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Drugcatalog")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrugCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "drug_id")
    private Integer drugId;

    @Column(nullable = false, length = 300)
    private String name; // Tên thuốc

    @Column(length = 1000)
    private String description; // Mô tả thuốc

    private LocalDateTime createdAt;

    // Một loại thuốc có thể nằm trong nhiều chi tiết toa thuốc
    @OneToMany(mappedBy = "drug", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PrescriptionDetail> prescriptionDetails;
}