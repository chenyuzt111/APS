package com.benewake.system.entity.enums;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum SchedulingResultType {
    APS_PRODUCTION_PLAN( 40, "成品生产计划" ,"apsProductionPlanServiceImpl"),
    APS_ALL_PLAN_NUM_IN_PROCESS( 41, "成品生产计划甘特图" ,"apsAllPlanNumInProcessServiceImpl"),
    APS_SEMI_FINISHED_GOODS_PRODUCTION_PLAN( 42, "半成品生产计划" ,"apsSemiFinishedGoodsProductionPlanServiceImpl"),
    APS_MATERIAL_SHORTAGE_ANALYSIS( 44, "成品缺料分析" ,"apsMaterialShortageAnalysisServiceImpl"),
    APS_SEMI_FINISHED_GOODS_MATERIAL_SHORTAGE_ANALYSIS( 46, "半成品缺料分析","apsSemiFinishedGoodsMaterialShortageAnalysisServiceImpl"),
    APS_FIM_PRIORITY( 48, "FIM需求优先级","apsFimPriorityServiceImpl");

    private Integer code;

    private String tableChName;

    private String serviceName;


    SchedulingResultType(Integer code, String tableChName, String serviceName) {
        this.code = code;
        this.tableChName = tableChName;
        this.serviceName = serviceName;
    }

    public static SchedulingResultType valueOfCode(Integer code) {
        if (code == null) {
            return null;
        }

        for (SchedulingResultType orderTag : SchedulingResultType.values()) {
            if (Objects.equals(orderTag.getCode(), code)) {
                return orderTag;
            }
        }

        return null;
    }

    public static int getCodeByServiceName(String serviceName) {
        for (SchedulingResultType value : SchedulingResultType.values()) {
            if (value.serviceName.equals(serviceName)) {
                return value.code;
            }
        }
        return -1; // 或者抛出异常
    }


}
