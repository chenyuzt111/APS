package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.transfer.KingdeeToApsImmediatelyInventory;
import com.benewake.system.entity.ApsImmediatelyInventory;
import com.benewake.system.entity.kingdee.KingdeeImmediatelyInventory;
import com.benewake.system.entity.kingdee.transfer.MaterialIdToName;
import com.benewake.system.service.ApsImmediatelyInventoryService;
import com.benewake.system.mapper.ApsImmediatelyInventoryMapper;
import com.benewake.system.service.ApsTableVersionService;
import com.kingdee.bos.webapi.entity.QueryParam;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
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
    private ApsTableVersionService apsTableVersionService;

    @Override
    public Boolean updateDataVersions() throws Exception {
        //获取转换后的数据
        ArrayList<ApsImmediatelyInventory> immediatelyInventories = getApsImmediatelyInventoriesByKingdee();
        return saveBatch(immediatelyInventories);
    }

    private ArrayList<ApsImmediatelyInventory> getApsImmediatelyInventoriesByKingdee() throws Exception {
        //获取金蝶原数据
        List<KingdeeImmediatelyInventory> result = getKingdeeImmediatelyInventories();
        //获取物料映射表
        Map<String, String> materialIdToNameMap = getMaterialIdToNameMap();

        ArrayList<ApsImmediatelyInventory> immediatelyInventories = new ArrayList<>();
        transferKingdeeToApsImmediatelyInventory(result, materialIdToNameMap, immediatelyInventories);
        return immediatelyInventories;
    }

    private void transferKingdeeToApsImmediatelyInventory(List<KingdeeImmediatelyInventory> result, Map<String, String> materialIdToNameMap, ArrayList<ApsImmediatelyInventory> immediatelyInventories) {
        //获取最大版本
        Integer maxVersion = apsTableVersionService.getMaxVersion();
        for (KingdeeImmediatelyInventory kingdeeImmediatelyInventory : result) {
            String materialId = kingdeeImmediatelyInventory.getFMaterialId(); // 获取物料ID
            if (materialIdToNameMap.containsKey(materialId)) {
                String materialNumber = materialIdToNameMap.get(materialId); // 获取物料编号
                kingdeeImmediatelyInventory.setFMaterialId(materialNumber); // 更新物料ID为物料编号
            }

            ApsImmediatelyInventory apsImmediatelyInventory = kingdeeToApsImmediatelyInventory.convert(kingdeeImmediatelyInventory, maxVersion);
            immediatelyInventories.add(apsImmediatelyInventory);
        }
    }

    private List<KingdeeImmediatelyInventory> getKingdeeImmediatelyInventories() throws Exception {
        //拼接sql
        QueryParam queryParam = new QueryParam()
                .setFormId("STK_Inventory")
                .setFieldKeys("FMaterialId,FStockName,FAVBQty,FLot,FExpiryDate");
        List<String> excludedStockNames = Arrays.asList(
                "不良品仓", "销售仓-SZ", "销售仓-北醒", "原材料仓BW", "X产品仓", "返品售后仓", "原材料仓-永新", "半成品仓-永新", "产成品仓-永新"
        );
        List<String> queryFilters = new ArrayList<>();
        queryFilters.add("FStockOrgId = 1");     // 库存组织==北醒(北京)光子科技有限公司
        queryFilters.add("FStockStatusId = 10000");       // 库存状态==可用
        for (String stockName : excludedStockNames) {
            queryFilters.add("FStockName != '" + stockName + "'");
        }
        queryParam.setFilterString(String.join(" and ", queryFilters));
        List<KingdeeImmediatelyInventory> result = api.executeBillQuery(queryParam, KingdeeImmediatelyInventory.class);
        return result;
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




