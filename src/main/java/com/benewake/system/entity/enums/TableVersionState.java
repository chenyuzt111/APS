package com.benewake.system.entity.enums;


public enum TableVersionState {
    SUCCESS(1, "成功"),
    EDITING(2, "编辑中"),
    INVALID(3, "失效");

    private final int code;
    private final String description;

    TableVersionState(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TableVersionState valueOf(int code) {
        for (TableVersionState state : values()) {
            if (state.code == code) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid EntityState code: " + code);
    }
}
