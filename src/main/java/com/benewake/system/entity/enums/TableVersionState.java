package com.benewake.system.entity.enums;


public enum TableVersionState {
    SUCCESS(1, "成功"),
    EDITING(2, "编辑中"),
    INVALID(3, "失效"),
    INTEGRIT_CHECKER(4, "完整性检查完成"),
    SCHEDULING(5, "排程完成"),
    UPDATE_DATABASE_ING(6, "更新数据库过程中"),
    INTEGRIT_CHECKER_ING(7, "完整性检查过程中"),
    SCHEDULING_ING(8, "排程中"),
    ONE_KEY_SCHEDULING_ING(9, "一键排程中");

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
