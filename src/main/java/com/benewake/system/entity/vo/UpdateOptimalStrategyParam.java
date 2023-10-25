package com.benewake.system.entity.vo;

import lombok.Data;

@Data
public class UpdateOptimalStrategyParam {
    private String productFamily;

    private int number;

//    策略 1- 时间 2-平衡率优先
    private int strategy;
}
