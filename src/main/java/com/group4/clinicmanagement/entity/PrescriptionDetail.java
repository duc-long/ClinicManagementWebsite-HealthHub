package com.group4.clinicmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "prescriptiondetail")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"prescription", "drug"})
public class PrescriptionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Integer detailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id", nullable = false)
    private DrugCatalog drug;

    @Column(name = "dosage", length = 100)
    private String dosage;

    @Column(name = "frequency", length = 200)
    private String frequency;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "instruction", length = 500)
    private String instruction;
}