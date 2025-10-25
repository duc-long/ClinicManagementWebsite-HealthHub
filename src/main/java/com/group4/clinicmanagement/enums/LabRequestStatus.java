package com.group4.clinicmanagement.enums;

public enum LabRequestStatus {
    REQUESTED(0),
    PAID(1),
    COMPLETED(2),
    CANCELLED(3),
    RUNNING(4);

    private final int value;

    LabRequestStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static LabRequestStatus fromInt(int value) {
        for (LabRequestStatus status : values()) {
            if (status.value == value) return status;
        }
        return REQUESTED;
    }
}
