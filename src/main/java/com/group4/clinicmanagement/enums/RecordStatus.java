package com.group4.clinicmanagement.enums;

public enum RecordStatus {
    OPEN(0),
    CLOSED(1);

    private final int value;

    RecordStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static RecordStatus fromInt(int value) {
        for (RecordStatus s : values()) {
            if (s.value == value) return s;
        }
        return OPEN;
    }
}
