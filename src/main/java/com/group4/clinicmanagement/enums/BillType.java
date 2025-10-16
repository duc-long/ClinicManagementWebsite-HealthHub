package com.group4.clinicmanagement.enums;

public enum BillType {
    CONSULATION(0),
    LAB_TEST(1);


    private final int value;

    BillType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static BillType fromInt(int value) {
        for (BillType status : values()) {
            if (status.getValue() == value) return status;
        }
        throw new IllegalArgumentException("Invalid BillType value: " + value);
    }
}
