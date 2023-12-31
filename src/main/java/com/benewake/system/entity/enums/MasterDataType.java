package com.benewake.system.entity.enums;

import com.benewake.system.entity.*;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
public enum MasterDataType {
    PROCESS_NAME_POOL(60, "工序命名池", "apsProcessNamePoolServiceImpl"),
    PROCESS_CAPACITY(61, "工序与产能", "apsProcessCapacityServiceImpl"),
    PROCESS_SCHEME(62, "基础工艺方案", "apsProcessSchemeServiceImpl"),
    PRODUCT_FAMILY_PROCESS_SCHEME_MANAGEMENT(63, "最终工艺方案", "apsProductFamilyProcessSchemeManagementServiceImpl"),
    SEMI_FINISHED_BASIC_DATA(64, "半成品基础数据", "apsSemiFinishedBasicDataServiceImpl"),
    FINISHED_PRODUCT_BASIC_DATA(65, "成品基础数据", "apsFinishedProductBasicDataServiceImpl"),
    RAW_MATERIAL_BASIC_DATA(66, "原材料基础数据", "apsRawMaterialBasicDataServiceImpl"),
    ATTENDANCE(67, "出勤管理", "apsAttendanceServiceImpl"),
    PRODUCT_FAMILY_MACHINE(68, "机器管理", "apsProductFamilyMachineTableServiceImpl");


    private final int code;

    private final String cnTableName;

    private final String seviceName;


    public static List<Integer> getAllIds() {
        ArrayList<Integer> ids = new ArrayList<>();
        for (MasterDataType value : MasterDataType.values()) {
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

    public static MasterDataType valueOfCode(Integer code) {
        if (code == null) {
            return null;
        }

        for (MasterDataType orderTag : MasterDataType.values()) {
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

        for (MasterDataType interfaceDataType : MasterDataType.values()) {
            if (interfaceDataType.getCode() == code) {
                return interfaceDataType.getSeviceName();
            }
        }

        return null;
    }

    public static int getCodeByServiceName(String serviceName) {
        for (MasterDataType value : MasterDataType.values()) {
            if (value.seviceName.equals(serviceName)) {
                return value.code;
            }
        }
        // 如果找不到匹配的serviceName，可以返回一个特定的值或抛出异常，具体取决于你的需求
        return -1; // 或者抛出异常
    }
}
