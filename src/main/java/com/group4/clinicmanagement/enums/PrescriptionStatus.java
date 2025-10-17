package com.group4.clinicmanagement.enums;

public enum PrescriptionStatus {
    ACTIVE(0),
    CANCELED(1),
    COMPLETED(2);

    private final int value;

    PrescriptionStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PrescriptionStatus fromInt(int value) {
        for (PrescriptionStatus s : values()) {
            if (s.value == value) return s;
        }
        return ACTIVE;
    }
}
