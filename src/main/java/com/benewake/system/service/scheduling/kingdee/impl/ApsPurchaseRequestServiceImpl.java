package com.benewake.system.service.scheduling.kingdee.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsOrder;
import com.benewake.system.entity.ApsPurchaseRequest;
import com.benewake.system.entity.ApsPurchaseRequestsOrders;
import com.benewake.system.entity.dto.ApsPurchaseRequestDto;
import com.benewake.system.entity.kingdee.KingdeePurchaseRequest;
import com.benewake.system.entity.kingdee.transfer.CreateIdToName;
import com.benewake.system.entity.kingdee.transfer.FBILLTYPEIDToName;
import com.benewake.system.entity.kingdee.transfer.MaterialIdToName;
import com.benewake.system.mapper.ApsPurchaseRequestMapper;
import com.benewake.system.service.scheduling.kingdee.ApsPurchaseRequestService;
import com.benewake.system.service.scheduling.kingdee.ApsPurchaseRequestsOrdersBaseService;
import com.benewake.system.transfer.KingdeeToApsPurchaseRequest;
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
 * @author ASUS
 * @description 针对表【aps_purchase_request】的数据库操作Service实现
 * @createDate 2023-10-08 09:31:46
 */
@Service
public class ApsPurchaseRequestServiceImpl extends ApsPurchaseRequestsOrdersBaseService
        implements ApsPurchaseRequestService {

    @Autowired
    private K3CloudApi api;


    @Autowired
    private KingdeeToApsPurchaseRequest kingdeeToApsPurchaseRequest;

    @Override
    public String getServiceName() {
        return "采购申请单列表";
    }

    @Override
    public List<ApsPurchaseRequestsOrders> getKingdeeDates() throws Exception {
        // 创建人和审核人映射表
        Map<String, String> fNameFUserIdMap = getFNameFuserIdMap();
        //单据映射表
        Map<String, String> fieldIdToBillTypeIdMap = getfieldIdToBillTypeIdMap();
        List<KingdeePurchaseRequest> kingdeePurchaseRequestList = getKingdeePurchaseRequests(fNameFUserIdMap, fieldIdToBillTypeIdMap);
        // 物料映射表
        Map<String, String> materialIdToNumberMap = getMaterialIdToNumberMap();

        //获取转换后的
        List<ApsPurchaseRequestsOrders> apsPurchaseRequestList = getApsPurchaseRequestArrayList(kingdeePurchaseRequestList, materialIdToNumberMap);

        return apsPurchaseRequestList;
    }





    private List<ApsPurchaseRequestsOrders> getApsPurchaseRequestArrayList(List<KingdeePurchaseRequest> kingdeePurchaseRequestList, Map<String, String> materialIdToNumberMap) throws NoSuchFieldException, IllegalAccessException, ParseException {
        List<ApsPurchaseRequestsOrders> apsPurchaseRequestList = new ArrayList<>();

        for (KingdeePurchaseRequest kingdeePurchaseRequest : kingdeePurchaseRequestList) {
            kingdeePurchaseRequest.setFMaterialId(materialIdToNumberMap.get(kingdeePurchaseRequest.getFMaterialId()));
            ApsPurchaseRequestsOrders apsPurchaseRequest = kingdeeToApsPurchaseRequest.convert(kingdeePurchaseRequest);
            apsPurchaseRequestList.add(apsPurchaseRequest);
        }
        return apsPurchaseRequestList;
    }



    private List<KingdeePurchaseRequest> getKingdeePurchaseRequests(Map<String, String> fNameFUserIdMap, Map<String, String> fieldIdToBillTypeIdMap) throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("PUR_Requisition");
        queryParam.setFieldKeys("FBillNo ,FMaterialId,FMaterialName,FBaseUnitQty,FArrivalDate");

        List<String> queryFilters = new ArrayList<>();
        queryFilters.add("FBillTypeID = '" + fieldIdToBillTypeIdMap.get("标准采购申请") + "'");
        queryFilters.add("FMRPTerminateStatus = 'A'");
        queryFilters.add("FMRPCloseStatus = 'A'");
        queryFilters.add("FDocumentStatus = 'C'");
        queryFilters.add("FCloseStatus = 'A'");
        queryFilters.add("FCancelStatus = 'A'");
        queryFilters.add("FOrderJoinQty = '0' ");
        queryFilters.add("(FCreatorId = '" + fNameFUserIdMap.get("王小溪") + "' OR FCreatorId = '" + fNameFUserIdMap.get("葛婧") + "')");
        queryParam.setFilterString(String.join(" and ", queryFilters));
        List<KingdeePurchaseRequest> kingdeePurchaseRequestList = api.executeBillQuery(queryParam, KingdeePurchaseRequest.class);
        return kingdeePurchaseRequestList;
    }



}




