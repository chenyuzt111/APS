package com.benewake.system.entity.enums;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
public enum InterfaceDataType {

    //APS
    IMMEDIATELY_INVENTORY(1, "即时库存", "apsImmediatelyInventoryServiceImpl"),
    OUTSOURCED_MATERIALS(2, "委外用料清单列表", "apsOutsourcedMaterialServiceImpl"),
    PRODUCTION_MATERIA(3, "生产用料清单列表", "apsProductionMaterialServiceImpl"),
    OUTSOURCED_ORDER(4, "委外订单列表", "apsOutsourcedOrderServiceImpl"),
    PRODUCTION_ORDER(5, "生产订单列表", "apsProductionOrderServiceImpl"),
    PURCHASE_REQUEST(6, "采购申请单列表", "apsPurchaseRequestServiceImpl"),
    PURCHASE_ORDERS(7, "采购订单列表", "apsPurchaseOrderServiceImpl"),
    RECEIVE_NOTICE(8, "收料通知单列表", "apsReceiveNoticeServiceImpl"),
    INVENTORY_LOCK(9, " 库存锁库列表", "apsInventoryLockServiceImpl"),
    MATERIAL_BOM(10, " 物料清单列表", "apsMaterialBomServiceImpl"),
    // MES向下
    PCBA_BURN(11, "PCBA烧录1", "apsPcbaBurnServiceImpl"),
    TFMINI__S_PCBA_BURN(12, "TFmini-S-PCBA烧录1", "apsTfminiSPcbaBurnServiceImpl"),
    PCBA_VERSION(13, "PCBA分版1", "apsPcbaVersionServiceImpl"),
    TFMINI_S_PCBA_VERSION(14, "TFmini-S-PCBA分版1", "apsTfminiSPcbaVersionServiceImpl"),
    INSTALLATION_BOARD(15, "安装主板1", "apsInstallationBoardServiceImpl"),
    TFMINI_S_INSTALLATION_BOARD(16, "TFmini-s-安装主板1", "apsTfminiSInstallationBoardServiceImpl"),
    SN_LABELING(17, "贴SN1", "apsSnLabelingServiceImpl"),
    TFMINI_S_SN_LABELING(18, "TFmini-s-贴SN", "apsTfminiSSnLabelingServiceImpl"),
    CALIBRATION_TESTS(19, "校验测试1", "apsCalibrationTestsServiceImpl"),
    TFMINI_S_CALIBRATION_TESTS(20, "TFmini-S-校验测试1", "apsTfminiSCalibrationTestsServiceImpl"),
    PACKAGING_TEST(21, "包装校验1", "apsPackagingTestServiceImpl"),
    TFMINI_S_PACKAGING_TEST(22, "TFmini-S-包装校验1", "apsTfminiSPackagingTestServiceImpl");


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

    public static List<Integer> getErpIds() {
        ArrayList<Integer> ids = new ArrayList<>();
        for (int i = 1; i <= MATERIAL_BOM.getCode(); i++) {
            ids.add(i);
        }
        return ids;
    }

    public static List<Integer> getMesIds() {
        ArrayList<Integer> ids = new ArrayList<>();
        for (int i = PCBA_BURN.getCode(); i <= TFMINI_S_PACKAGING_TEST.getCode(); i++) {
            ids.add(i);
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
