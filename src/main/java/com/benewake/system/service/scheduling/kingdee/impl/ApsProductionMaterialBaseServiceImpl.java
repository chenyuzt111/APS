package com.benewake.system.service.scheduling.kingdee.impl;

import com.benewake.system.entity.ApsMaterial;
import com.benewake.system.entity.enums.FMaterialStatus;
import com.benewake.system.entity.kingdee.KingdeeProductionMaterial;
import com.benewake.system.entity.kingdee.SubQuery;
import com.benewake.system.service.scheduling.kingdee.ApsMaterialBaseService;
import com.benewake.system.service.scheduling.kingdee.ApsProductionMaterialService;
import com.benewake.system.transfer.ProductionKingdeeToApsMaterial;
import com.kingdee.bos.webapi.entity.QueryParam;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ASUS
 * @description 针对表【aps_production_material】的数据库操作Service实现
 * @createDate 2023-10-07 16:50:37
 */
@Service
public class ApsProductionMaterialBaseServiceImpl extends ApsMaterialBaseService implements ApsProductionMaterialService {

    @Autowired
    private K3CloudApi api;

    @Autowired
    private ProductionKingdeeToApsMaterial kingdeeToApsProductionMaterial;



    private List<ApsMaterial> getApsProductionMaterials(List<KingdeeProductionMaterial> result, Map<String, String> mtn) throws NoSuchFieldException, IllegalAccessException {
        List<ApsMaterial> apsProductionMaterials = new ArrayList<>();

        for (KingdeeProductionMaterial kingdeeProductionMaterial : result) {
            String materialIdName = mtn.get(kingdeeProductionMaterial.getFMaterialID());
            String materialId2Name = mtn.get(kingdeeProductionMaterial.getFMaterialID2());

            kingdeeProductionMaterial.setFMaterialID(materialIdName);
            kingdeeProductionMaterial.setFMaterialID2(materialId2Name);

            String fMaterialType = kingdeeProductionMaterial.getFMaterialType();
            String fMaterialTypeDescription = FMaterialStatus.getByCode(fMaterialType).getDescription();
            kingdeeProductionMaterial.setFMaterialType(fMaterialTypeDescription);
            ApsMaterial apsProductionMaterial = kingdeeToApsProductionMaterial.convert(kingdeeProductionMaterial);
            apsProductionMaterials.add(apsProductionMaterial);
        }
        return apsProductionMaterials;
    }


    private List<KingdeeProductionMaterial> getProUses(List<SubQuery> subMoQueryResult) throws Exception {
        List<String> subMoBillNos = new ArrayList<>();
        for (SubQuery subQueryRow : subMoQueryResult) {
            String subMoBillNo = subQueryRow.getFBillNo(); // 获取子查询结果中的数据
            subMoBillNos.add("'" + subMoBillNo + "'"); // 添加到列表，并添加单引号以进行比较
        }

        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("PRD_PPBOM");
        queryParam.setFieldKeys("FMaterialID,FMaterialName,FMOBillNO,FMaterialID2,FMaterialName1,FMaterialType,FMustQty,FPickedQty,FGoodReturnQty,FProcessDefectReturnQty");

        // 条件筛选
        List<String> queryFilters = new ArrayList<>();

        // 使用子查询结果作为条件
        if (!subMoBillNos.isEmpty()) {
            String subMoBillNosCondition = "FMOBillNO in (" + String.join(",", subMoBillNos) + ")";
            queryFilters.add(subMoBillNosCondition);
        }
        queryFilters.add("FDocumentStatus = 'C' AND FWorkshopID = 102714 AND (FMoEntryStatus = '3' OR FMoEntryStatus = '4' OR FMoEntryStatus = '5')");     // 单据状态=已审核

        queryParam.setFilterString(String.join(" and ", queryFilters));
//        queryParam.setLimit(100);

        List<KingdeeProductionMaterial> result = api.executeBillQuery(queryParam, KingdeeProductionMaterial.class);
//        int a = 1;
        return result;
    }


    @Override
    public List<ApsMaterial> getKingdeeDates() throws Exception {
        List<SubQuery> subMoQueryResult = getPrdMo();
        // 从子查询结果中提取 FBillNo 列，存储在列表中
        List<KingdeeProductionMaterial> result = getProUses(subMoQueryResult);
        // 物料映射表
        Map<String, String> mtn = getMaterialIdToNameMap();
        //转换
        return getApsProductionMaterials(result, mtn);
    }
}




