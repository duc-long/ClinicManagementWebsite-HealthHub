package com.group4.clinicmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Prescriptiondetail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer detailId;

    @ManyToOne
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne
    @JoinColumn(name = "drug_id", nullable = false)
    private DrugCatalog drug;

    private Integer quantity;
    private String dosage;
    private String frequency;
    private Integer duration_days;
    private String instruction;
}
