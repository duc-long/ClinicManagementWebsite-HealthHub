package com.group4.clinicmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "DoctorDailySlot")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDailySlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id")
    private Long slotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    @Column(name = "total_slots", nullable = false)
    private int totalSlots = 25;

    @Column(name = "available_slots", nullable = false)
    private int availableSlots = 25;

    @PrePersist
    @PreUpdate
    private void validateSlots() {
        if (availableSlots > totalSlots) {
            throw new IllegalArgumentException("availableSlots cannot exceed totalSlots");
        }
        if (availableSlots < 0 || totalSlots < 0) {
            throw new IllegalArgumentException("Slots cannot be negative");
        }
    }

}