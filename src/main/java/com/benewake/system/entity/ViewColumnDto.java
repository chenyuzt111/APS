package com.benewake.system.entity;

import lombok.Data;

@Data
public class ViewColumnDto {
    private Long id;
    private Long viewId;
    private Long colId;
    private String chColName;
    private String enColName;
    private String voColName;
    private String valueOperator;
    private String colValue;
    private Integer colSeq;
}
