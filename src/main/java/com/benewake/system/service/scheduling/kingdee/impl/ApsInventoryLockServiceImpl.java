package com.benewake.system.service.scheduling.kingdee.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsInventoryLock;
import com.benewake.system.entity.dto.ApsInventoryLockDto;
import com.benewake.system.entity.kingdee.KingdeeInventoryLock;
import com.benewake.system.entity.kingdee.transfer.MaterialIdToName;
import com.benewake.system.entity.kingdee.transfer.fLotIdToFNumber;
import com.benewake.system.mapper.ApsInventoryLockMapper;
import com.benewake.system.service.scheduling.kingdee.ApsInventoryLockService;
import com.benewake.system.transfer.KingdeeToApsInventoryLock;
import com.kingdee.bos.webapi.entity.QueryParam;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author ASUS
* @description 针对表【aps_inventory_lock(用于存储库存锁定信息的表)】的数据库操作Service实现
* @createDate 2023-10-13 10:01:44
*/
@Service
public class ApsInventoryLockServiceImpl extends ServiceImpl<ApsInventoryLockMapper, ApsInventoryLock>
        implements ApsInventoryLockService {

    @Autowired
    private K3CloudApi api;


    @Autowired
    private KingdeeToApsInventoryLock kingdeeToApsInventoryLock;

    @Autowired
    private ApsInventoryLockMapper apsInventoryLockMapper;

    Integer maxVersion;

    @Override
    public Boolean updateDataVersions() throws Exception {
        maxVersion = this.getMaxVersionIncr();
        List<KingdeeInventoryLock> result = getKingdeeInventoryLockList();
        // 物料映射表
        Map<String, String> materialIdToNameMap = getMaterialIdToNameMap();
        Map<String, String> lotIdToFNumberMap = getLotIdToFNumberMap();
        ArrayList<ApsInventoryLock> apsInventoryLockList = getApsInventoryLockList(result, materialIdToNameMap ,lotIdToFNumberMap);
        if (CollectionUtils.isEmpty(apsInventoryLockList)) {
            ApsInventoryLock apsInventoryLock = new ApsInventoryLock();
            apsInventoryLock.setVersion(maxVersion);
            return save(apsInventoryLock);
        }
        return saveBatch(apsInventoryLockList);
    }

    public Map<String, String> getLotIdToFNumberMap() throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("BD_BatchMainFile");
        queryParam.setFieldKeys("FLOTID,FNumber");
        List<fLotIdToFNumber> fLotIdToFNumbers = api.executeBillQuery(queryParam, fLotIdToFNumber.class);
        Map<String, String> fLotIdToFNumberMap = new HashMap<>();
        fLotIdToFNumbers.forEach(fLotIdToFNumber -> {
            fLotIdToFNumberMap.put(fLotIdToFNumber.getFLOTID(), fLotIdToFNumber.getFNumber());
        });
        return fLotIdToFNumberMap;
    }

    @Override
    public void insertVersionIncr() {
        apsInventoryLockMapper.insertSelectVersionIncr();
    }

    @Override
    public Page selectPageList(Page page, List tableVersionList) {
        Page<ApsInventoryLockDto> apsInventoryLockDtoPage = apsInventoryLockMapper.selectPageList(page ,tableVersionList);
        return apsInventoryLockDtoPage;
    }

    @Override
    public List searchLike(List versionToChVersionArrayList, QueryWrapper queryWrapper) {
        return apsInventoryLockMapper.searchLike(versionToChVersionArrayList, queryWrapper);
    }

    @Override
    public Page selectPageLists(Page page, List versionToChVersionArrayList, QueryWrapper wrapper) {
        String customSqlSegment = wrapper.getCustomSqlSegment();
        if (StringUtils.isEmpty(customSqlSegment) || !customSqlSegment.contains("ORDER BY")) {
            wrapper.orderByDesc("ch_version_name");
            wrapper.orderByAsc("f_material_id");
        }
        return apsInventoryLockMapper.selectPageLists(page, versionToChVersionArrayList, wrapper);
    }

    private ArrayList<ApsInventoryLock> getApsInventoryLockList(List<KingdeeInventoryLock> result, Map<String, String> materialIdToNameMap, Map<String, String> lotIdToFNumberMap) throws NoSuchFieldException, IllegalAccessException, ParseException {
        ArrayList<ApsInventoryLock> apsInventoryLockList = new ArrayList<>();

        for (KingdeeInventoryLock kingdeeInventoryLock : result) {
            // 获取 FDocumentStatus 的 id
            kingdeeInventoryLock.setFMaterialId(materialIdToNameMap.get(kingdeeInventoryLock.getFMaterialId()));
            String lot = kingdeeInventoryLock.getFLot();
            kingdeeInventoryLock.setFLot(lotIdToFNumberMap.getOrDefault(lot ,lotIdToFNumberMap + "-----对应表内不存在"));
            ApsInventoryLock apsInventoryLock = kingdeeToApsInventoryLock.convert(kingdeeInventoryLock ,maxVersion);
            apsInventoryLockList.add(apsInventoryLock);
        }
        return apsInventoryLockList;
    }

    private Map<String, String> getMaterialIdToNameMap() throws Exception {
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

    private List<KingdeeInventoryLock> getKingdeeInventoryLockList() throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("STK_LockStock");
        queryParam.setFieldKeys("FMaterialId,FMaterialName,FEXPIRYDATE,FLockQty,FLot");
        List<String> queryFilters = new ArrayList<>();
        queryFilters.add("FStockOrgId = 1");
        queryParam.setFilterString(String.join(" and ", queryFilters));
        List<KingdeeInventoryLock> result = api.executeBillQuery(queryParam, KingdeeInventoryLock.class);
        return result;
    }
}




