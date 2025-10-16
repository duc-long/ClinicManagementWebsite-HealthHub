package com.group4.clinicmanagement.enums;

public enum BillStatus {
    PENDING(0),
    PAID(1),
    VOID(2);

    private final int value;

    BillStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static BillStatus fromInt(int value) {
        for (BillStatus status : values()) {
            if (status.getValue() == value) return status;
        }
        throw new IllegalArgumentException("Invalid BillStatus value: " + value);
    }
}
