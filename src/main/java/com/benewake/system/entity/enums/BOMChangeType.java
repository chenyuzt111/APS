package com.benewake.system.entity.enums;

public enum BOMChangeType {
    CHANGE_BEFORE("0", "变更前"),
    CHANGE_AFTER("1", "变更后"),
    ADD("2", "新增"),
    DELETE("3", "删除"),
    MAIN_MATERIAL("5", "主物料");

    private final String code;
    private final String description;

    BOMChangeType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    // 根据代码获取相应的枚举值
    public static BOMChangeType getByCode(String code) {
        for (BOMChangeType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null; // 或者抛出异常，具体取决于您的需求
    }
}
