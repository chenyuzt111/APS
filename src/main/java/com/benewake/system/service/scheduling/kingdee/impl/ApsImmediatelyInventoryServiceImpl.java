package com.benewake.system.service.scheduling.kingdee.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsImmediatelyInventory;
import com.benewake.system.entity.dto.ApsImmediatelyInventoryDto;
import com.benewake.system.entity.kingdee.KingdeeImmediatelyInventory;
import com.benewake.system.entity.kingdee.KingdeeInventoryLock;
import com.benewake.system.entity.kingdee.transfer.MaterialIdToName;
import com.benewake.system.entity.kingdee.transfer.fLotIdToFNumber;
import com.benewake.system.mapper.ApsImmediatelyInventoryMapper;
import com.benewake.system.service.scheduling.kingdee.ApsImmediatelyInventoryService;
import com.benewake.system.transfer.KingdeeToApsImmediatelyInventory;
import com.kingdee.bos.webapi.entity.QueryParam;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author ASUS
 * @description 针对表【immediately_inventory】的数据库操作Service实现
 * @createDate 2023-10-06 14:19:08
 */
@Service
public class ApsImmediatelyInventoryServiceImpl extends ServiceImpl<ApsImmediatelyInventoryMapper, ApsImmediatelyInventory>
        implements ApsImmediatelyInventoryService {

    @Autowired
    private K3CloudApi api;

    @Autowired
    private KingdeeToApsImmediatelyInventory kingdeeToApsImmediatelyInventory;


    @Autowired
    private ApsImmediatelyInventoryMapper apsImmediatelyInventoryMapper;

    @Override
    public List searchLike(List versionToChVersionArrayList, QueryWrapper queryWrapper) {
        return apsImmediatelyInventoryMapper.searchLike(versionToChVersionArrayList, queryWrapper);
    }

    @Override
    public Page selectPageList(Page objectPage, List tableVersionList) {
        Page<ApsImmediatelyInventoryDto> immediatelyInventoryPage = apsImmediatelyInventoryMapper
                .selectPageList(objectPage, tableVersionList);
        return immediatelyInventoryPage;
    }

    @Override
    public Page selectPageLists(Page page, List versionToChVersionArrayList, QueryWrapper wrapper) {
        String customSqlSegment = wrapper.getCustomSqlSegment();
        if (StringUtils.isEmpty(customSqlSegment) || !customSqlSegment.contains("ORDER BY")) {
            //chVersion降序 即时库存 -> 版本5 -> 版本4 -> 版本3 -> 版本2 -> 版本1
            wrapper.orderByDesc("ch_version_name");
            wrapper.orderByAsc("f_material_id");
            wrapper.orderByAsc("f_lot");
        }
        return apsImmediatelyInventoryMapper.selectPageLists(page, versionToChVersionArrayList, wrapper);
    }

    @Override
    public Boolean updateDataVersions() throws Exception {
        Integer maxVersion = this.getMaxVersionIncr();
        //获取转换后的数据
        ArrayList<ApsImmediatelyInventory> immediatelyInventories = getApsImmediatelyInventoriesByKingdee(maxVersion);
        if (CollectionUtils.isEmpty(immediatelyInventories)) {
            ApsImmediatelyInventory apsImmediatelyInventory = new ApsImmediatelyInventory();
            apsImmediatelyInventory.setVersion(maxVersion);
            return save(apsImmediatelyInventory);
        }
        return saveBatch(immediatelyInventories);
    }

    //迭代一个版本
    @Override
    public void insertVersionIncr() {
        apsImmediatelyInventoryMapper.insertSelectVersionIncr();
    }


    private ArrayList<ApsImmediatelyInventory> getApsImmediatelyInventoriesByKingdee(Integer maxVersion) throws Exception {
        //获取金蝶原数据
        List<KingdeeImmediatelyInventory> result = getKingdeeImmediatelyInventories();
        //获取物料映射表
        Map<String, String> materialIdToNameMap = getMaterialIdToNameMap();
        Map<String, String> lotIdToFNumberMap = getLotIdToFNumberMap();
        ArrayList<ApsImmediatelyInventory> immediatelyInventories = new ArrayList<>();
        transferKingdeeToApsImmediatelyInventory(maxVersion, result, materialIdToNameMap, immediatelyInventories, lotIdToFNumberMap);
        return immediatelyInventories;
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

    private void transferKingdeeToApsImmediatelyInventory(Integer maxVersion, List<KingdeeImmediatelyInventory> result, Map<String, String> materialIdToNameMap,
                                                          ArrayList<ApsImmediatelyInventory> immediatelyInventories, Map<String, String> lotIdToFNumberMap) throws Exception {
        QueryParam queryParamLock = new QueryParam();
        queryParamLock.setFormId("STK_LockStock");
        queryParamLock.setFieldKeys("FMaterialId,FEXPIRYDATE,FLockQty,FLot");
        List<KingdeeInventoryLock> locks = api.executeBillQuery(queryParamLock, KingdeeInventoryLock.class);
        for (KingdeeImmediatelyInventory kingdeeImmediatelyInventory : result) {
            String materialId = kingdeeImmediatelyInventory.getFMaterialId();
            String lot = kingdeeImmediatelyInventory.getFLot();
            // 初始化 avbQty 为 FBaseQty 的值
            int avbQty = kingdeeImmediatelyInventory.getFBaseQty();
            // 在子查询结果中查找匹配的 Lock 记录
            for (KingdeeInventoryLock lock : locks) {
                if (materialId.equals(lock.getFMaterialId()) && lot.equals(lock.getFLot())) {
                    int lockQty = lock.getFLockQty();
                    avbQty = avbQty - lockQty; // 计算可用数量
                    break;
                }
            }
            String fMaterialId = kingdeeImmediatelyInventory.getFMaterialId();
            String s = materialIdToNameMap.get(fMaterialId);
            kingdeeImmediatelyInventory.setFMaterialId(s);
            // 更新 FAVBQty 字段
            kingdeeImmediatelyInventory.setFAVBQty(avbQty);

            kingdeeImmediatelyInventory.setFLot(lotIdToFNumberMap.getOrDefault(lot, lot + "---对应表内不存在"));
            ApsImmediatelyInventory apsImmediatelyInventory = kingdeeToApsImmediatelyInventory.convert(kingdeeImmediatelyInventory, maxVersion);

            immediatelyInventories.add(apsImmediatelyInventory);
        }
    }

    private List<KingdeeImmediatelyInventory> getKingdeeImmediatelyInventories() throws Exception {
        //拼接sql
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("STK_Inventory");
        queryParam.setFieldKeys("FMaterialId,FMaterialName,FStockName,FBaseQty,FAVBQty,FLot,FExpiryDate");
        List<String> excludedStockNames = Arrays.asList("不良品仓", "销售仓-SZ", "销售仓-北醒", "原材料仓BW", "X产品仓", "返品售后仓", "原材料仓-永新", "半成品仓-永新", "产成品仓-永新");
        // 条件筛选
        List<String> queryFilters = new ArrayList<>();
        //创建一个空的字符串列表，用于存储查询过滤条件
        queryFilters.add("FStockOrgId = 1");     // 库存组织==北醒(北京)光子科技有限公司
        queryFilters.add("FStockStatusId = 10000");       // 库存状态==可用
        queryFilters.add("FBaseQty != 0");
        for (String stockName : excludedStockNames) {
            queryFilters.add("FStockName != '" + stockName + "'");
        }
        queryParam.setFilterString(String.join(" and ", queryFilters));
        return api.executeBillQuery(queryParam, KingdeeImmediatelyInventory.class);
    }

    private Map<String, String> getMaterialIdToNameMap() throws Exception {
        // 物料映射表
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

}