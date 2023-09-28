package com.benewake.system.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
public enum InterfaceDataType {

    REALTIME_INVENTORY(1, "即时库存" ,"realtimeInventorySevice" , "realtimeInventoryMapper"),
    OUTSOURCED_MATERIALS(2, "委外用料清单列表" , "OUTSOURCED_MATERIALSService" ,"OUTSOURCED_MATERIALSMapper");
    //TODO 多个表类型 这里就只先写俩个
    private int code;
    private String type;
    private String seviceName;
    private String mapperName;

    public String getSeviceName() {
        return seviceName;
    }

    public String getMapperName() {
        return mapperName;
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

    public static String mapperNameOfCode(Integer code) {
        if (code == null) {
            return null;
        }

        for (InterfaceDataType interfaceDataType : InterfaceDataType.values()) {
            if (interfaceDataType.getCode() == code) {
                return interfaceDataType.getMapperName();
            }
        }

        return null;
    }

}
