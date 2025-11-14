package com.group4.clinicmanagement.enums;

public enum AppointmentStatus {
    PENDING(0),       // waiting for confirmation
    CONFIRMED(1),     // doctor assigned
    CANCELLED(2),     // cancelled by patient or clinic
    PAID(3),          // bill paid
    NO_SHOW(4),       // patient did not show up
    CHECKED_IN(5),    // patient checked in at reception
    EXAMINED(6),      // doctor finished examination
    DONE(7);
    private final int value;

    AppointmentStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AppointmentStatus fromInt(int value) {
        for (AppointmentStatus status : values()) {
            if (status.value == value) return status;
        }
        return PENDING;
    }
}
