package com.group4.clinicmanagement.enums;

public enum Gender {
    UNKNOWN(0),
    MALE(1),
    FEMALE(2);

    private final int value;

    Gender(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Gender fromInt(int value) {
        for (Gender gender : values()) {
            if (gender.value == value) {
                return gender;
            }
        }
        return UNKNOWN;
    }
}
