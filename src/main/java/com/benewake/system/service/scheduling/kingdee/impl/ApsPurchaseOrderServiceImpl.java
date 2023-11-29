package com.benewake.system.service.scheduling.kingdee.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsPurchaseOrder;
import com.benewake.system.entity.dto.ApsPurchaseOrderDto;
import com.benewake.system.entity.kingdee.KingdeePurchaseOrder;
import com.benewake.system.entity.kingdee.transfer.CreateIdToName;
import com.benewake.system.entity.kingdee.transfer.MaterialIdToName;
import com.benewake.system.service.scheduling.kingdee.ApsPurchaseOrderService;
import com.benewake.system.mapper.ApsPurchaseOrderMapper;
import com.benewake.system.transfer.KingdeeToApsPurchaseOrder;
import com.kingdee.bos.webapi.entity.QueryParam;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author ASUS
 * @description 针对表【aps_purchase_order】的数据库操作Service实现
 * @createDate 2023-10-26 13:58:33
 */
@Service
public class ApsPurchaseOrderServiceImpl extends ServiceImpl<ApsPurchaseOrderMapper, ApsPurchaseOrder>
        implements ApsPurchaseOrderService {

    @Autowired
    private K3CloudApi api;

    @Autowired
    private KingdeeToApsPurchaseOrder kingdeeToApsPurchaseOrder;

    @Autowired
    private ApsPurchaseOrderMapper apsPurchaseOrderMapper;

    @Override
    public Boolean updateDataVersions() throws Exception {
        Integer maxVersionIncr = this.getMaxVersionIncr();
        List<KingdeePurchaseOrder> result = getKingdeePurchaseOrders();
        // 物料映射表
        Map<String, String> materialIdToNumber = getMaterialIdToNumber();
        //转换
        List<ApsPurchaseOrder> apsPurchaseOrders = new ArrayList<>();
        for (KingdeePurchaseOrder kingdeePurchaseOrder : result) {
            // 信息替换
            kingdeePurchaseOrder.setFMaterialId(materialIdToNumber.get(kingdeePurchaseOrder.getFMaterialId()));
            ApsPurchaseOrder apsPurchaseOrder = kingdeeToApsPurchaseOrder.convert(kingdeePurchaseOrder, maxVersionIncr);
            apsPurchaseOrders.add(apsPurchaseOrder);
        }
        if (CollectionUtils.isEmpty(apsPurchaseOrders)) {
            ApsPurchaseOrder apsPurchaseOrder = new ApsPurchaseOrder();
            apsPurchaseOrder.setVersion(maxVersionIncr);
            return save(apsPurchaseOrder);
        }
        return saveBatch(apsPurchaseOrders);
    }

    @Override
    public void insertVersionIncr() {
        apsPurchaseOrderMapper.insertSelectVersionIncr();
    }

    @Override
    public Page selectPageList(Page page, List tableVersionList) {
        Page<ApsPurchaseOrderDto> apsPurchaseOrderDtoPage = apsPurchaseOrderMapper.selectPageList(page, tableVersionList);
        return apsPurchaseOrderDtoPage;
    }

    private Map<String, String> getMaterialIdToNumber() throws Exception {
        QueryParam queryParam;
        queryParam = new QueryParam();
        queryParam.setFormId("BD_MATERIAL");
        queryParam.setFieldKeys("FMaterialId,FNumber");
        List<MaterialIdToName> midToName = api.executeBillQuery(queryParam, MaterialIdToName.class);
        Map<String, String> mtn = new HashMap<>();
        midToName.forEach(c -> {
            mtn.put(c.getFMaterialId(), c.getFNumber());
        });
        return mtn;
    }

    private List<KingdeePurchaseOrder> getKingdeePurchaseOrders() throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("SEC_User");
        queryParam.setFieldKeys("FUserID,FName");
        List<CreateIdToName> crtidToName = api.executeBillQuery(queryParam, CreateIdToName.class);
        Map<String, String> critn = new HashMap<>();
        crtidToName.forEach(c -> {
            critn.put(c.getFName(), c.getFUserID());
        });
        String Liu = critn.get("刘赛");
        String Song = critn.get("宋雨朦");
        String Zhang = critn.get("张月");
        queryParam.setFormId("PUR_PurchaseOrder");
        queryParam.setFieldKeys("FBillNo,FMaterialId,FMaterialName,FRemainReceiveQty,FDeliveryDate_Plan");
        List<String> queryFilters = new ArrayList<>();
        queryFilters.add("FMRPTerminateStatus = 'A' and FPurchaseOrgId= '1' and FMRPCloseStatus = 'A' and FMRPFreezeStatus = 'A' and FCloseStatus = 'A' and FSupplierId != 336724 and FCancelStatus = 'A' and FDate > dateAdd(day, -365, getdate()) and FCreatorId !=" + Liu + " and FCreatorId !=" + Song + "and FCreatorId !=" + Zhang); // 业务终止=正常

        queryParam.setFilterString(String.join(" and ", queryFilters));
        List<KingdeePurchaseOrder> result = api.executeBillQuery(queryParam, KingdeePurchaseOrder.class);
        return result;
    }

}




