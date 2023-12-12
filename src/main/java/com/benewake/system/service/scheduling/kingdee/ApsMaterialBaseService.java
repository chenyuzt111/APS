package com.benewake.system.service.scheduling.kingdee;

import com.benewake.system.entity.ApsMaterial;
import com.benewake.system.entity.kingdee.SubQuery;
import com.benewake.system.entity.kingdee.transfer.MaterialIdToName;
import com.kingdee.bos.webapi.entity.QueryParam;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public abstract class ApsMaterialBaseService {

    @Autowired
    private K3CloudApi api;

    public List<String> getSubReqBillNos() throws Exception {
        QueryParam subQueryParam = new QueryParam();
        subQueryParam.setFormId("SUB_SUBREQORDER");
        subQueryParam.setFieldKeys("FBillNo");
        List<String> queryFilters = new ArrayList<>();
        queryFilters.add("FPickMtrlStatus = '2'");
        subQueryParam.setFilterString(String.join(" and ", queryFilters));
        List<SubQuery> subQueryResult = api.executeBillQuery(subQueryParam, SubQuery.class);
        // 从子查询结果中提取 FBillNo 列，存储在列表中
        List<String> subReqBillNos  = new ArrayList<>();
        for (SubQuery subQueryRow : subQueryResult) {
            String subReqBillNo = subQueryRow.getFBillNo(); // 获取子查询结果中的数据
            subReqBillNos.add("'" + subReqBillNo + "'"); // 添加到列表，并添加单引号以进行比较
        }
        return subReqBillNos;
    }

    public Map<String, String> getMaterialIdToNameMap() throws Exception {
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


    protected List<SubQuery> getPrdMo() throws Exception {
        //子查询中取出FBillNo 列
        QueryParam subQueryParam = new QueryParam();
        subQueryParam.setFormId("PRD_MO");
        subQueryParam.setFieldKeys("FBillNo");
        subQueryParam.setFilterString("FPickMtrlStatus = '2'");
        List<SubQuery> subMoQueryResult = api.executeBillQuery(subQueryParam, SubQuery.class);
        return subMoQueryResult;
    }

    public abstract List<ApsMaterial> getKingdeeDates() throws Exception;

}
