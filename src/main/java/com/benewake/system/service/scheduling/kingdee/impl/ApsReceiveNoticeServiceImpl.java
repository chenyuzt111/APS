package com.benewake.system.service.scheduling.kingdee.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsReceiveNotice;
import com.benewake.system.entity.kingdee.KingdeeReceiveNotice;
import com.benewake.system.entity.kingdee.transfer.CreateIdToName;
import com.benewake.system.entity.kingdee.transfer.MaterialIdToName;
import com.benewake.system.mapper.ApsReceiveNoticeMapper;
import com.benewake.system.service.scheduling.kingdee.ApsReceiveNoticeService;
import com.benewake.system.transfer.KingdeeToApsReceiveNotice;
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
 * @description 针对表【aps_receive_notice】的数据库操作Service实现
 * @createDate 2023-10-08 10:45:23
 */
@Service
public class ApsReceiveNoticeServiceImpl extends ServiceImpl<ApsReceiveNoticeMapper, ApsReceiveNotice>
        implements ApsReceiveNoticeService {


    @Autowired
    private K3CloudApi api;


    @Autowired
    private KingdeeToApsReceiveNotice kingdeeToApsReceiveNotice;

    @Autowired
    private ApsReceiveNoticeMapper apsReceiveNoticeMapper;

    Integer maxVersion;

    @Override
    public Boolean updateDataVersions() throws Exception {
        maxVersion = this.getMaxVersionIncr();
        // 创建人和审核人映射表
        Map<String, String> fNameToFUserIdMap = getStringStringMap();

        List<KingdeeReceiveNotice> result = getKingdeeReceiveNotices(fNameToFUserIdMap);
        // 物料映射表
        Map<String, String> materialIdToNameMap = getmaterialIdToNameMap();
        //获取转换后的List
        ArrayList<ApsReceiveNotice> apsReceiveNoticeList = getApsReceiveNoticeList(result, materialIdToNameMap);
        if (CollectionUtils.isEmpty(apsReceiveNoticeList)) {
            ApsReceiveNotice apsReceiveNotice = new ApsReceiveNotice();
            apsReceiveNotice.setVersion(maxVersion);
            return save(apsReceiveNotice);
        }
        return saveBatch(apsReceiveNoticeList);
    }

    @Override
    public List<Object> selectVersionPageList(Integer pass, Integer size, List versionToChVersionArrayList) {
        return (List<Object>) apsReceiveNoticeMapper.selectVersionPageList(pass, size, versionToChVersionArrayList);
    }

    private ArrayList<ApsReceiveNotice> getApsReceiveNoticeList(List<KingdeeReceiveNotice> result, Map<String, String> materialIdToNameMap) throws NoSuchFieldException, IllegalAccessException {
        ArrayList<ApsReceiveNotice> apsReceiveNoticeList = new ArrayList<>();

        for (KingdeeReceiveNotice kingdeeReceiveNotice : result) {
            // 信息替换
            kingdeeReceiveNotice.setFMaterialId(materialIdToNameMap.get(kingdeeReceiveNotice.getFMaterialId()));
            ApsReceiveNotice apsReceiveNotice = kingdeeToApsReceiveNotice.convert(kingdeeReceiveNotice, maxVersion);
            apsReceiveNoticeList.add(apsReceiveNotice);
        }
        return apsReceiveNoticeList;
    }

    private Map<String, String> getmaterialIdToNameMap() throws Exception {
        QueryParam queryParam;
        queryParam = new QueryParam();
        queryParam.setFormId("BD_MATERIAL");
        queryParam.setFieldKeys("FMaterialId,FNumber");
        List<MaterialIdToName> midToNameList = api.executeBillQuery(queryParam, MaterialIdToName.class);
        Map<String, String> materialIdToNameMap = new HashMap<>();
        midToNameList.forEach(materialIdToName -> {
            materialIdToNameMap.put(materialIdToName.getFMaterialId(), materialIdToName.getFNumber());
        });
        return materialIdToNameMap;
    }

    private List<KingdeeReceiveNotice> getKingdeeReceiveNotices(Map<String, String> fNameToFUserIdMap) throws Exception {
        QueryParam queryParam;
        queryParam = new QueryParam();
        queryParam.setFormId("PUR_ReceiveBill");
        queryParam.setFieldKeys("FBillNo,FMaterialId,FMaterialName,FMustQty,FCheckQty,FReceiveQty,FCsnReceiveBaseQty,FInStockQty");

        List<String> queryFilters = new ArrayList<>();
        queryFilters.add("FDocumentStatus = 'C'");    // 单据状态=已审核
        queryFilters.add("FENTRYSTATUS = 'A'");          // 行状态=正常
        queryFilters.add("FCancelStatus = 'A'");      // 作废状态=未作废
        queryFilters.add("FPurOrgId   = 1");
        queryFilters.add("FCreatorId != '" + fNameToFUserIdMap.get("刘赛") + "'");
        queryFilters.add("FCreatorId != '" + fNameToFUserIdMap.get("张月") + "'");
        queryFilters.add("FCreatorId != '" + fNameToFUserIdMap.get("杨阳") + "'");
        queryFilters.add("FCreatorId != '" + fNameToFUserIdMap.get("郑杰") + "'");
        queryFilters.add("FCreatorId != '" + fNameToFUserIdMap.get("宋雨朦") + "'");

        queryParam.setFilterString(String.join(" and ", queryFilters));
        return api.executeBillQuery(queryParam, KingdeeReceiveNotice.class);
    }

    private Map<String, String> getStringStringMap() throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("SEC_User");
        queryParam.setFieldKeys("FUserID,FName");
        List<CreateIdToName> crtidToNameList = api.executeBillQuery(queryParam, CreateIdToName.class);
        Map<String, String> fNameToFUserIdMap = new HashMap<>();
        crtidToNameList.forEach(createIdToName -> fNameToFUserIdMap.put(createIdToName.getFName(), createIdToName.getFUserID()));
        return fNameToFUserIdMap;
    }
}




