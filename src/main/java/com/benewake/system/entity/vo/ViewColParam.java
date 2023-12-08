package com.benewake.system.entity.vo;


import lombok.Data;

@Data
public class ViewColParam {
    private Integer id;

    private Integer colId;

    private String enColName;

    private String valueOperator;

    private String colValue;

    private Long viewId;

    private Integer colSeq;
}
