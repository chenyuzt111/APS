package com.benewake.system.service.scheduling.kingdee.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsProductionMaterial;
import com.benewake.system.entity.dto.ApsProductionMaterialDto;
import com.benewake.system.entity.enums.FMaterialStatus;
import com.benewake.system.entity.kingdee.KingdeeProductionMaterial;
import com.benewake.system.entity.kingdee.YourResultClassForSubQuery;
import com.benewake.system.entity.kingdee.transfer.MaterialIdToName;
import com.benewake.system.mapper.ApsProductionMaterialMapper;
import com.benewake.system.service.scheduling.kingdee.ApsProductionMaterialService;
import com.benewake.system.transfer.KingdeeToApsProductionMaterial;
import com.kingdee.bos.webapi.entity.QueryParam;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author ASUS
* @description 针对表【aps_production_material】的数据库操作Service实现
* @createDate 2023-10-07 16:50:37
*/
@Service
public class ApsProductionMaterialServiceImpl extends ServiceImpl<ApsProductionMaterialMapper, ApsProductionMaterial>
    implements ApsProductionMaterialService {

    @Autowired
    private K3CloudApi api;

    @Autowired
    private KingdeeToApsProductionMaterial kingdeeToApsProductionMaterial;

    @Autowired
    private ApsProductionMaterialMapper apsProductionMaterialMapper;

    private Integer maxVersion;

    @Override
    public Boolean updateDataVersions() throws Exception {
        maxVersion = this.getMaxVersionIncr();
        List<YourResultClassForSubQuery> subMoQueryResult = getYourResultClassForSubQueries();
        // 从子查询结果中提取 FBillNo 列，存储在列表中
        List<KingdeeProductionMaterial> result = getProUses(subMoQueryResult);
        // 物料映射表
        Map<String, String> mtn = getMaterialIdToNameMap();
        //转换
        ArrayList<ApsProductionMaterial> apsProductionMaterials = getApsProductionMaterials(result, mtn);
        if (CollectionUtils.isEmpty(apsProductionMaterials)) {
            ApsProductionMaterial apsProductionMaterial = new ApsProductionMaterial();
            apsProductionMaterial.setVersion(maxVersion);
            return save(apsProductionMaterial);
        }
        return saveBatch(apsProductionMaterials);
    }

    @Override
    public void insertVersionIncr() {
        apsProductionMaterialMapper.insertSelectVersionIncr();
    }

    @Override
    public Page selectPageList(Page page, List tableVersionList) {
        Page<ApsProductionMaterialDto> productionMaterialPage = apsProductionMaterialMapper.selectPageList(page ,tableVersionList);
        return productionMaterialPage;
    }



    private ArrayList<ApsProductionMaterial> getApsProductionMaterials( List<KingdeeProductionMaterial> result, Map<String, String> mtn) throws NoSuchFieldException, IllegalAccessException {
        ArrayList<ApsProductionMaterial> apsProductionMaterials = new ArrayList<>();

        for (KingdeeProductionMaterial kingdeeProductionMaterial : result) {
            String materialIdName = mtn.get(kingdeeProductionMaterial.getFMaterialID());
            String materialId2Name = mtn.get(kingdeeProductionMaterial.getFMaterialID2());

            kingdeeProductionMaterial.setFMaterialID(materialIdName);
            kingdeeProductionMaterial.setFMaterialID2(materialId2Name);

            String fMaterialType = kingdeeProductionMaterial.getFMaterialType();
            String fMaterialTypeDescription = FMaterialStatus.getByCode(fMaterialType).getDescription();
            kingdeeProductionMaterial.setFMaterialType(fMaterialTypeDescription);
            ApsProductionMaterial apsProductionMaterial = kingdeeToApsProductionMaterial.convert(kingdeeProductionMaterial, maxVersion);
            apsProductionMaterials.add(apsProductionMaterial);
        }
        return apsProductionMaterials;
    }

    private Map<String, String> getMaterialIdToNameMap() throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("BD_MATERIAL");
        queryParam.setFieldKeys("FMaterialId,FNumber");
        List<MaterialIdToName> midToName = api.executeBillQuery(queryParam, MaterialIdToName.class);
        Map<String, String> mtn = new HashMap<>();
        midToName.forEach(c -> {
            mtn.put(c.getFMaterialId(), c.getFNumber());
        });
        return mtn;
    }

    private List<KingdeeProductionMaterial> getProUses(List<YourResultClassForSubQuery> subMoQueryResult) throws Exception {
        List<String> subMoBillNos = new ArrayList<>();
        for (YourResultClassForSubQuery subQueryRow : subMoQueryResult) {
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

    private List<YourResultClassForSubQuery> getYourResultClassForSubQueries() throws Exception {
        //子查询中取出FBillNo 列
        QueryParam subQueryParam = new QueryParam();
        subQueryParam.setFormId("PRD_MO");
        subQueryParam.setFieldKeys("FBillNo");
        subQueryParam.setFilterString("FPickMtrlStatus = '2'");
        List<YourResultClassForSubQuery> subMoQueryResult = api.executeBillQuery(subQueryParam, YourResultClassForSubQuery.class);
        return subMoQueryResult;
    }
}




