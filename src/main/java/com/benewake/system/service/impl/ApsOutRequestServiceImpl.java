package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsOutRequest;
import com.benewake.system.entity.dto.ApsOutRequestDto;
import com.benewake.system.entity.kingdee.KingdeeOutRequest;
import com.benewake.system.entity.kingdee.transfer.MaterialIdToName;
import com.benewake.system.mapper.ApsOutRequestMapper;
import com.benewake.system.service.ApsOutRequestService;
import com.benewake.system.transfer.KingdeeToApsOutRequest;
import com.kingdee.bos.webapi.entity.QueryParam;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author DELL
 * @description 针对表【aps_out_request】的数据库操作Service实现
 * @createDate 2023-11-13 10:57:34
 */
@Service
public class ApsOutRequestServiceImpl extends ServiceImpl<ApsOutRequestMapper, ApsOutRequest>
        implements ApsOutRequestService {

    @Autowired
    private K3CloudApi api;

    @Autowired
    private KingdeeToApsOutRequest kingdeeToApsOutRequest;

    @Autowired
    private ApsOutRequestMapper apsOutRequestMapper;


    @Override
    public Boolean updateDataVersions() throws Exception {
        List<KingdeeOutRequest> result = getKingdeeOutRequest();
        // 物料映射表
        Map<String, String> mtn = getMaterialIdToNameMap();
        ArrayList<ApsOutRequest> apsOutRequests = new ArrayList<>();
        Integer maxVersion = this.getMaxVersionIncr();
        for (KingdeeOutRequest kingdeeOutRequest : result) {
            getApsOutRequestList(maxVersion, mtn, apsOutRequests, kingdeeOutRequest);
        }
        if (CollectionUtils.isEmpty(apsOutRequests)) {
            ApsOutRequest apsOutRequest = new ApsOutRequest();
            apsOutRequest.setVersion(maxVersion);
            return save(apsOutRequest);
        }
        return saveBatch(apsOutRequests);
    }


    private void getApsOutRequestList(Integer maxVersion, Map<String, String> mtn, ArrayList<ApsOutRequest> apsOutRequests, KingdeeOutRequest kingdeeOutRequest) throws ParseException {
        kingdeeOutRequest.setFMaterialId(mtn.get(kingdeeOutRequest.getFMaterialId()));
        ApsOutRequest apsOutRequest = kingdeeToApsOutRequest.convert(kingdeeOutRequest, maxVersion);
        apsOutRequests.add(apsOutRequest);
    }

    private Map<String, String> getMaterialIdToNameMap() throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("BD_MATERIAL");
        queryParam.setFieldKeys("FMaterialId,FNumber");
        List<MaterialIdToName> midToName = api.executeBillQuery(queryParam, MaterialIdToName.class);
        Map<String, String> mtn = new HashMap<>();
        midToName.forEach(c -> mtn.put(c.getFMaterialId(), c.getFNumber()));
        return mtn;
    }

    private List<KingdeeOutRequest> getKingdeeOutRequest() throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("STK_OutStockApply");
        queryParam.setFieldKeys("FMaterialId,F_ora_BackDate");
        // 条件筛选
        List<String> queryFilters = new ArrayList<>();
        //创建一个空的字符串列表，用于存储查询过滤条件
        queryFilters.add("FBizType=4");

        queryParam.setFilterString(String.join(" and ", queryFilters));

        List<KingdeeOutRequest> result = api.executeBillQuery(queryParam, KingdeeOutRequest.class);
        return result;
    }

    @Override
    public void insertVersionIncr() {
        apsOutRequestMapper.insertVersionIncr();
    }

    @Override
    public Page selectPageList(Page page, List tableVersionList) {
        Page<ApsOutRequestDto> apsOutRequestDtoPage = apsOutRequestMapper.selectPageList(page, tableVersionList);
        return apsOutRequestDtoPage;
    }
}

