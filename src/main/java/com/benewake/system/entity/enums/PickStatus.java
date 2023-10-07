package com.benewake.system.entity.enums;

public enum PickStatus {
    STANDARD("1", "标准件"),
    RETURN("2", "返还件"),
    SUBSTITUTE("3", "替代件");

    private final String code;
    private final String description;

    private PickStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PickStatus getByCode(String code) {
        for (PickStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
