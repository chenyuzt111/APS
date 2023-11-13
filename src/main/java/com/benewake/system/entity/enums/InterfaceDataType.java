package com.benewake.system.entity.enums;

import com.benewake.system.entity.*;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
public enum InterfaceDataType {

    //APS
    IMMEDIATELY_INVENTORY(1, "即时库存", "apsImmediatelyInventoryServiceImpl", ApsImmediatelyInventory.class),
    OUTSOURCED_MATERIALS(2, "委外用料清单列表", "apsOutsourcedMaterialServiceImpl", ApsOutsourcedMaterial.class),
    PRODUCTION_MATERIA(3, "生产用料清单列表", "apsProductionMaterialServiceImpl", ApsProductionMaterial.class),
    OUTSOURCED_ORDER(4, "委外订单列表", "apsOutsourcedOrderServiceImpl", ApsOutsourcedOrder.class),
    PRODUCTION_ORDER(5, "生产订单列表", "apsProductionOrderServiceImpl", ApsProductionOrder.class),
    PURCHASE_REQUEST(6, "采购申请单列表", "apsPurchaseRequestServiceImpl", ApsPurchaseRequest.class),
    PURCHASE_ORDERS(7, "采购订单列表", "apsPurchaseOrderServiceImpl", ApsPurchaseOrder.class),
    RECEIVE_NOTICE(8, "收料通知单列表", "apsReceiveNoticeServiceImpl", ApsReceiveNotice.class),
    INVENTORY_LOCK(9, "库存锁库列表", "apsInventoryLockServiceImpl", ApsInventoryLock.class),
    MATERIAL_BOM(10, "物料清单列表", "apsMaterialBomServiceImpl", ApsMaterialBom.class),
//    APS_MATERIAL_NAME_MAPPING(11, "物料编码与名称对应表", "apsMaterialNameMappingServiceImpl", ApsMaterialNameMapping.class),
    OUT_REQUEST(11, "出库申请单", "apsOutRequestImpl", ApsOutRequest.class),
    // MES向下
    PCBA_BURN(20, "PCBA烧录1", "apsPcbaBurnServiceImpl", ApsPcbaBurn.class),
    TFMINI_S_PCBA_BURN(21, "TFmini-S-PCBA烧录", "apsTfminiSPcbaBurnServiceImpl", ApsTfminiSPcbaBurn.class),
    PCBA_VERSION(22, "PCBA分版1", "apsPcbaVersionServiceImpl", ApsPcbaVersion.class),
    TFMINI_S_PCBA_VERSION(23, "TFmini-S-PCBA分版1", "apsTfminiSPcbaVersionServiceImpl", ApsTfminiSPcbaVersion.class),
    INSTALLATION_BOARD(24, "安装主板1", "apsInstallationBoardServiceImpl", ApsInstallationBoard.class),
    TFMINI_S_INSTALLATION_BOARD(25, "TFmini-s-安装主板1", "apsTfminiSInstallationBoardServiceImpl", ApsTfminiSInstallationBoard.class),
    SN_LABELING(26, "贴SN1", "apsSnLabelingServiceImpl", ApsSnLabeling.class),
    TFMINI_S_SN_LABELING(27, "TFmini-s-贴SN", "apsTfminiSSnLabelingServiceImpl", ApsTfminiSSnLabeling.class),
    CALIBRATION_TESTS(28, "校验测试1", "apsCalibrationTestsServiceImpl", ApsCalibrationTests.class),
    TFMINI_S_CALIBRATION_TESTS(29, "TFmini-S-校验测试1", "apsTfminiSCalibrationTestsServiceImpl", ApsTfminiSCalibrationTests.class),
    PACKAGING_TEST(30, "包装校验1", "apsPackagingTestServiceImpl", ApsPackagingTest.class),
    TFMINI_S_PACKAGING_TEST(31, "TFmini-S-包l 装校验1", "apsTfminiSPackagingTestServiceImpl", ApsTfminiSPackagingTest.class);


    private int code;
    private String cnTableName;
    private String seviceName;
    private Class classs;

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

    public Class getClasss() {
        return classs;
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
