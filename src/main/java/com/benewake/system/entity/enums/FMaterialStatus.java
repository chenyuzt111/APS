package com.benewake.system.entity.enums;

/**
 * 子项类型
 */
public enum FMaterialStatus {
    STANDARD("1", "标准件"),
    RETURN("2", "返还件"),
    SUBSTITUTE("3", "替代件");

    private final String code;
    private final String description;

    private FMaterialStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static FMaterialStatus getByCode(String code) {
        for (FMaterialStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
