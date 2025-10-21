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

    public static UserStatus fromInt(int value) {
        for (UserStatus status : values()) {
            if (status.getValue() == value) return status;
        }
       return INACTIVE;
    }
}
