package com.benewake.system.entity.enums;

public enum ExcelOperationEnum {
    APPEND(1, "追加"),
    OVERRIDE(2, "覆盖"),
    CURRENT_PAGE(3, "当前页"),
    ALL_PAGES(4, "全部");

    private final int code;
    private final String description;

    ExcelOperationEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
