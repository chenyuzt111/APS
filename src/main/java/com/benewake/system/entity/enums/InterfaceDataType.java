package com.benewake.system.entity.enums;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
public enum InterfaceDataType {

    IMMEDIATELY_INVENTORY(1, "即时库存" ,"apsImmediatelyInventoryServiceImpl"),
    OUTSOURCED_MATERIALS(2, "委外用料清单列表" , "apsOutsourcedMaterialServiceImpl" ),
    PRODUCTION_MATERIA(3, "生产用料清单列表" , "apsProductionMaterialServiceImpl"),
    OUTSOURCED_ORDER(4, "委外订单列表" , "apsOutsourcedOrderServiceImpl"),
    PRODUCTION_ORDER(5, "生产订单列表" , "apsProductionOrderServiceImpl"),
    PURCHASE_REQUEST(6, "采购申请单列表" , "apsPurchaseRequestServiceImpl"),
    //TODO 8 访问不通
    RECEIVE_NOTICE(8, "收料通知单列表" , "apsReceiveNoticeServiceImpl"),
    INVENTORY_LOCK(9, "库存锁库列表" , "apsInventoryLockServiceImpl");
    //TODO 多个表类型 这里就只先写俩个
    private int code;
    private String type;
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
