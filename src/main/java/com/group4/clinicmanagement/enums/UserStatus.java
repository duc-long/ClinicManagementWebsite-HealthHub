package com.group4.clinicmanagement.enums;

public enum UserStatus {
    INACTIVE(0),
    ACTIVE(1);

    private final int value;

    UserStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserStatus fromValue(int value) {
        return switch (value) {
            case 1 -> ACTIVE;
            case 0 -> INACTIVE;
            default -> INACTIVE;
        };
    }
}
