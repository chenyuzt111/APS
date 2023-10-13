package com.benewake.system.entity.enums;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
public enum InterfaceDataType {

    IMMEDIATELY_INVENTORY(1, "即时库存", "apsImmediatelyInventoryServiceImpl"),
    OUTSOURCED_MATERIALS(2, "委外用料清单列表", "apsOutsourcedMaterialServiceImpl"),
    PRODUCTION_MATERIA(3, "生产用料清单列表", "apsProductionMaterialServiceImpl"),
    OUTSOURCED_ORDER(4, "委外订单列表", "apsOutsourcedOrderServiceImpl"),
    PRODUCTION_ORDER(5, "生产订单列表", "apsProductionOrderServiceImpl"),
    PURCHASE_REQUEST(6, "采购申请单列表", "apsPurchaseRequestServiceImpl"),
    //TODO 7 访问不通
    RECEIVE_NOTICE(8, "收料通知单列表", "apsReceiveNoticeServiceImpl"),
    INVENTORY_LOCK(9, "库存锁库列表", "apsInventoryLockServiceImpl");
    //TODO 多个表类型
    private int code;
    private String cnTableName;
    private String seviceName;

    public static List<Integer> getAllIds() {
        ArrayList<Integer> ids = new ArrayList<>();
        for (InterfaceDataType value : InterfaceDataType.values()) {
            ids.add(value.getCode());
        }
        return ids;
    }

    public String getSeviceName() {
        return seviceName;
    }

    public int getCode() {
        return code;
    }

    public String getCnTableName() {
        return cnTableName;
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

    public static int getCodeByServiceName(String serviceName) {
        for (InterfaceDataType value : InterfaceDataType.values()) {
            if (value.seviceName.equals(serviceName)) {
                return value.code;
            }
        }
        // 如果找不到匹配的serviceName，可以返回一个特定的值或抛出异常，具体取决于你的需求
        return -1; // 或者抛出异常
    }
}
