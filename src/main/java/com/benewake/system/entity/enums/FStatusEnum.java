package com.benewake.system.entity.enums;

/**
 * 业务状态
 */
public enum FStatusEnum {
    PLAN("1", "计划"),
    PLAN_CONFIRM("2", "计划确认"),
    RELEASE("3", "下达"),
    STARTED("4", "开工"),
    COMPLETED("5", "完工"),
    CLOSED("6", "结案"),
    SETTLED("7", "结算");

    private final String code;
    private final String description;

    private FStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static FStatusEnum getByCode(String code) {
        for (FStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
