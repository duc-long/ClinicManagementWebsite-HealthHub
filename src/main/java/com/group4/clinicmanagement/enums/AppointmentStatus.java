package com.group4.clinicmanagement.enums;

public enum AppointmentStatus {
    PENDING(0),
    CONFIRMED(1),
    CANCELLED(2),
    COMPLETED(3),
    NO_SHOW(4),
    CHECKED_IN(5);

    private final int value;

    AppointmentStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AppointmentStatus fromInt(int value) {
        for (AppointmentStatus status : values()) {
            if (status.getValue() == value) return status;
        }
        throw new IllegalArgumentException("Invalid AppointmentStatus value: " + value);
    }
}
