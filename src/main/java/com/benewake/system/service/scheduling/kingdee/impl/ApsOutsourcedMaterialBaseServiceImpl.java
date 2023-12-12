package com.benewake.system.service.scheduling.kingdee.impl;

import com.benewake.system.entity.ApsMaterial;
import com.benewake.system.entity.enums.FMaterialStatus;
import com.benewake.system.entity.kingdee.KingdeeOutsourcedMaterial;
import com.benewake.system.service.scheduling.kingdee.ApsMaterialBaseService;
import com.benewake.system.service.scheduling.kingdee.ApsOutsourcedMaterialService;
import com.benewake.system.transfer.KingdeeOutsourcedToApsMaterial;
import com.kingdee.bos.webapi.entity.QueryParam;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* @author ASUS
* @description 针对表【aps_outsourced_material】的数据库操作Service实现
* @createDate 2023-10-07 15:47:59
*/
@Service
public class ApsOutsourcedMaterialBaseServiceImpl extends ApsMaterialBaseService implements ApsOutsourcedMaterialService {

    @Autowired
    private K3CloudApi api;


    @Autowired
    private KingdeeOutsourcedToApsMaterial kingdeeOutsourcedToApsMaterial;

    public List<ApsMaterial> getKingdeeDates() throws Exception {
        //子查询中取出FBillNo 列
        List<String> subReqBillNos = getSubReqBillNos();
        List<KingdeeOutsourcedMaterial> kingdeeOutsourcedMaterials = getKingdeeOutsourcedMaterial(subReqBillNos);
        // 获取物料映射表
        Map<String, String> materialIdToNameMap = getMaterialIdToNameMap();
        //获取转换后的数据
        return transferKingdeeToApsOutsourcedMaterial(kingdeeOutsourcedMaterials, materialIdToNameMap);
    }


    private List<ApsMaterial> transferKingdeeToApsOutsourcedMaterial(List<KingdeeOutsourcedMaterial> kingdeeOutsourcedMaterials, Map<String, String> materialIdToNameMap) throws NoSuchFieldException, IllegalAccessException {
        ArrayList<ApsMaterial> apsOutsourcedMaterials = new ArrayList<>();

        for (KingdeeOutsourcedMaterial kingdeeOutsourcedMaterial : kingdeeOutsourcedMaterials) {
            String materialIdName = materialIdToNameMap.get(kingdeeOutsourcedMaterial.getFMaterialID());
            String materialId2Name = materialIdToNameMap.get(kingdeeOutsourcedMaterial.getFMaterialID2());
            kingdeeOutsourcedMaterial.setFMaterialID(materialIdName);
            kingdeeOutsourcedMaterial.setFMaterialID2(materialId2Name);
            String fMaterialType = kingdeeOutsourcedMaterial.getFMaterialType();
            String fMaterialTypeDescription = FMaterialStatus.getByCode(fMaterialType).getDescription();
            kingdeeOutsourcedMaterial.setFMaterialType(fMaterialTypeDescription);

            ApsMaterial apsOutsourcedMaterial = kingdeeOutsourcedToApsMaterial.convert(kingdeeOutsourcedMaterial);
            apsOutsourcedMaterials.add(apsOutsourcedMaterial);
        }
        return apsOutsourcedMaterials;
    }


    private List<KingdeeOutsourcedMaterial> getKingdeeOutsourcedMaterial(List<String> subReqBillNos) throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("SUB_PPBOM");
        queryParam.setFieldKeys("FMaterialID,FMaterialName,FSubReqBillNO,FMaterialID2,FMaterialName2,FMaterialType,FMustQty,FPickedQty,FGoodReturnQty,FProcessDefectReturnQty");
        // 条件筛选
        List<String> queryFilters = new ArrayList<>();
        //创建一个空的字符串列表，用于存储查询过滤条件
        queryFilters.add("FDocumentStatus = 'C'");     // 单据状态=已审核
        queryFilters.add("(FReqStatus = '3'OR FReqStatus = '4' OR FReqStatus = '5')"); // 委外订单状态=下达
        // 使用子查询结果作为条件
        if (!subReqBillNos.isEmpty()) {
            String subReqBillNosCondition = "FSubReqBillNO in (" + String.join(",", subReqBillNos) + ")";
            queryFilters.add(subReqBillNosCondition);
        }
        queryParam.setFilterString(String.join(" and ", queryFilters));
//        queryParam.setLimit(100);
        List<KingdeeOutsourcedMaterial> result = api.executeBillQuery(queryParam, KingdeeOutsourcedMaterial.class);
        return result;
    }

}




