package com.benewake.system.service.scheduling.kingdee.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsOutsourcedMaterial;
import com.benewake.system.entity.dto.ApsOutsourcedMaterialDto;
import com.benewake.system.entity.enums.FMaterialStatus;
import com.benewake.system.entity.kingdee.KingdeeOutsourcedMaterial;
import com.benewake.system.entity.kingdee.YourResultClassForSubQuery;
import com.benewake.system.entity.kingdee.transfer.MaterialIdToName;
import com.benewake.system.service.scheduling.kingdee.ApsOutsourcedMaterialService;
import com.benewake.system.mapper.ApsOutsourcedMaterialMapper;
import com.benewake.system.transfer.KingdeeToApsOutsourcedMaterial;
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
* @description 针对表【aps_outsourced_material】的数据库操作Service实现
* @createDate 2023-10-07 15:47:59
*/
@Service
public class ApsOutsourcedMaterialServiceImpl extends ServiceImpl<ApsOutsourcedMaterialMapper, ApsOutsourcedMaterial>
    implements ApsOutsourcedMaterialService {

    @Autowired
    private K3CloudApi api;


    @Autowired
    private KingdeeToApsOutsourcedMaterial kingdeeToApsOutsourcedMaterial;

    @Autowired
    private ApsOutsourcedMaterialMapper apsOutsourcedMaterialMapper;

    private Integer maxVersion;
    @Override
    public Boolean updateDataVersions() throws Exception {
        //子查询中取出FBillNo 列
        maxVersion = this.getMaxVersionIncr();
        List<String> subReqBillNos = getsubReqBillNos();

        List<KingdeeOutsourcedMaterial> kingdeeOutsourcedMaterials = getKingdeeOutsourcedMaterial(subReqBillNos);
        // 获取物料映射表
        Map<String, String> materialIdToNameMap = getmaterialIdToNameMap();
        //获取转换后的数据
        ArrayList<ApsOutsourcedMaterial> apsOutsourcedMaterials = transferKingdeeToApsOutsourcedMaterial(kingdeeOutsourcedMaterials, materialIdToNameMap);
        if (CollectionUtils.isEmpty(apsOutsourcedMaterials)) {
            ApsOutsourcedMaterial apsOutsourcedMaterial = new ApsOutsourcedMaterial();
            apsOutsourcedMaterial.setVersion(maxVersion);
        }
        return saveBatch(apsOutsourcedMaterials);
    }

    @Override
    public void insertVersionIncr() {
        apsOutsourcedMaterialMapper.insertSelectVersionIncr();
    }

    @Override
    public Page selectPageList(Page page, List tableVersionList) {
        Page<ApsOutsourcedMaterialDto> outsourcedMaterialDtoPage = apsOutsourcedMaterialMapper.selectPageList(page ,tableVersionList);
        return outsourcedMaterialDtoPage;
    }



    private ArrayList<ApsOutsourcedMaterial> transferKingdeeToApsOutsourcedMaterial(List<KingdeeOutsourcedMaterial> kingdeeOutsourcedMaterials, Map<String, String> materialIdToNameMap) throws NoSuchFieldException, IllegalAccessException {
        ArrayList<ApsOutsourcedMaterial> apsOutsourcedMaterials = new ArrayList<>();

        for (KingdeeOutsourcedMaterial kingdeeOutsourcedMaterial : kingdeeOutsourcedMaterials) {
            String materialIdName = materialIdToNameMap.get(kingdeeOutsourcedMaterial.getFMaterialID());
            String materialId2Name = materialIdToNameMap.get(kingdeeOutsourcedMaterial.getFMaterialID2());
            kingdeeOutsourcedMaterial.setFMaterialID(materialIdName);
            kingdeeOutsourcedMaterial.setFMaterialID2(materialId2Name);
            String fMaterialType = kingdeeOutsourcedMaterial.getFMaterialType();
            String fMaterialTypeDescription = FMaterialStatus.getByCode(fMaterialType).getDescription();
            kingdeeOutsourcedMaterial.setFMaterialType(fMaterialTypeDescription);

            ApsOutsourcedMaterial apsOutsourcedMaterial = kingdeeToApsOutsourcedMaterial.convert(kingdeeOutsourcedMaterial, maxVersion);
            apsOutsourcedMaterials.add(apsOutsourcedMaterial);
        }
        return apsOutsourcedMaterials;
    }

    private Map<String, String> getmaterialIdToNameMap() throws Exception {
        Map<String, String> materialIdToNameMap = new HashMap<>();
        // 物料映射表
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("BD_MATERIAL");
        queryParam.setFieldKeys("FMaterialId,FNumber");
        List<MaterialIdToName> midToNames = api.executeBillQuery(queryParam, MaterialIdToName.class);
        midToNames.forEach(midToName -> {
            materialIdToNameMap.put(midToName.getFMaterialId(), midToName.getFNumber());
        });
        return materialIdToNameMap;
    }

    private List<KingdeeOutsourcedMaterial> getKingdeeOutsourcedMaterial(List<String> subReqBillNos) throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("SUB_PPBOM");
        queryParam.setFieldKeys("FMaterialID,FMaterialName,FSubReqBillNO,FMaterialID2,FMaterialName2,FMaterialType,FMustQty,FPickedQty,FGoodReturnQty,FProcessDefectReturnQty");
        // 条件筛选
        List<String> queryFilters = new ArrayList<>();
        //创建一个空的字符串列表，用于存储查询过滤条件
        queryFilters.add("FDocumentStatus = 'C'");     // 单据状态=已审核
        queryFilters.add("FReqStatus = '3'OR FReqStatus = '4' OR FReqStatus = '5'"); // 委外订单状态=下达
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

    private List<String> getsubReqBillNos() throws Exception {
        QueryParam subQueryParam = new QueryParam();
        subQueryParam.setFormId("SUB_SUBREQORDER");
        subQueryParam.setFieldKeys("FBillNo");
        subQueryParam.setFilterString("FPickMtrlStatus = '2'");
        List<YourResultClassForSubQuery> subQueryResult = api.executeBillQuery(subQueryParam, YourResultClassForSubQuery.class);

        // 从子查询结果中提取 FBillNo 列，存储在列表中
        List<String> subReqBillNos = new ArrayList<>();
        for (YourResultClassForSubQuery subQueryRow : subQueryResult) {
            String subReqBillNo = subQueryRow.getFBillNo(); // 获取子查询结果中的数据
            subReqBillNos.add("'" + subReqBillNo + "'"); // 添加到列表，并添加单引号以进行比较
        }
        return subReqBillNos;
    }
}




