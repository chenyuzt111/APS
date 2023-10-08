package com.benewake.system.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
public enum InterfaceDataType {

    IMMEDIATELY_INVENTORY(1, "即时库存" ,"apsImmediatelyInventoryServiceImpl"),
    OUTSOURCED_MATERIALS(2, "委外用料清单列表" , "apsOutsourcedMaterialServiceImpl" ),
    PRODUCTION_MATERIA(3, "生产用料清单列表" , "apsProductionMaterialServiceImpl"),
    OUTSOURCED_ORDER(4, "委外订单列表" , "apsOutsourcedOrderServiceImpl"),
    PRODUCTION_ORDER(5, "生产订单列表" , "apsProductionOrderServiceImpl");
    //TODO 多个表类型 这里就只先写俩个
    private int code;
    private String type;
    private String seviceName;


    public String getSeviceName() {
        return seviceName;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public static InterfaceDataType valueOfCode(Integer code) {
        if (code == null) {
            return null;
        }

        for (InterfaceDataType orderTag : InterfaceDataType.values()) {
            if (orderTag.getCode() == code) {
                return orderTag;
            }
        }

        return null;
    }

    public static String serviceNameOfCode(Integer code) {
        if (code == null) {
            return null;
        }

        for (InterfaceDataType interfaceDataType : InterfaceDataType.values()) {
            if (interfaceDataType.getCode() == code) {
                return interfaceDataType.getSeviceName();
            }
        }

        return null;
    }

}
