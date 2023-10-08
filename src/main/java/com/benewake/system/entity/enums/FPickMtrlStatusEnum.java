package com.benewake.system.entity.enums;

/**
 * 领料状态
 */

public enum FPickMtrlStatusEnum {
    NOT_PICKED("1", "未领料"),
    PARTIALLY_PICKED("2", "部分领料"),
    FULLY_PICKED("3", "全部领料"),
    OVER_PICKED("4", "超额领料");

    private final String code;
    private final String description;

    private FPickMtrlStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static FPickMtrlStatusEnum getByCode(String code) {
        for (FPickMtrlStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
