package com.benewake.system.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcessSchemeEntity {
    private int id;
    private String currentProcessScheme;
    private String employeeName;
    private BigDecimal standardTime;
}