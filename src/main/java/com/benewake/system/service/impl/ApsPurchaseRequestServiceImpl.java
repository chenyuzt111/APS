package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsPurchaseRequest;
import com.benewake.system.entity.kingdee.KingdeePurchaseRequest;
import com.benewake.system.entity.kingdee.transfer.CreateIdToName;
import com.benewake.system.entity.kingdee.transfer.FBILLTYPEIDToName;
import com.benewake.system.entity.kingdee.transfer.MaterialIdToName;
import com.benewake.system.service.ApsPurchaseRequestService;
import com.benewake.system.mapper.ApsPurchaseRequestMapper;
import com.benewake.system.transfer.KingdeeToApsPurchaseRequest;
import com.kingdee.bos.webapi.entity.QueryParam;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class ApsPurchaseRequestServiceImpl extends ServiceImpl<ApsPurchaseRequestMapper, ApsPurchaseRequest>
        implements ApsPurchaseRequestService {

    @Autowired
    private K3CloudApi api;


    @Autowired
    private KingdeeToApsPurchaseRequest kingdeeToApsPurchaseRequest;


    @Autowired
    private ApsPurchaseRequestMapper apsPurchaseRequestMapper;
    @Override
    public Boolean updateDataVersions() throws Exception {
        // 创建人和审核人映射表
        Map<String, String> fNameFUserIdMap = getFNameFuserIdMap();
        //单据映射表
        Map<String, String> fieldIdToBillTypeIdMap = getfieldIdToBillTypeIdMap();
        List<KingdeePurchaseRequest> kingdeePurchaseRequestList = getKingdeePurchaseRequests(fNameFUserIdMap, fieldIdToBillTypeIdMap);
        // 物料映射表
        Map<String, String> materialIdToNumberMap = getMaterialIdToNumberMap();

        //获取转换后的
        ArrayList<ApsPurchaseRequest> apsPurchaseRequestList = getApsPurchaseRequestArrayList(kingdeePurchaseRequestList, materialIdToNumberMap);

        return saveBatch(apsPurchaseRequestList);
    }

    @Override
    public List<Object> selectVersionPageList(Integer pass, Integer size, List versionToChVersionArrayList) {
        return (List<Object>) apsPurchaseRequestMapper.selectVersionPageList(pass, size, versionToChVersionArrayList);
    }

    private ArrayList<ApsPurchaseRequest> getApsPurchaseRequestArrayList(List<KingdeePurchaseRequest> kingdeePurchaseRequestList, Map<String, String> materialIdToNumberMap) throws NoSuchFieldException, IllegalAccessException {
        ArrayList<ApsPurchaseRequest> apsPurchaseRequestList = new ArrayList<>();
        Integer maxVersion = this.getMaxVersionIncr();
        for (KingdeePurchaseRequest kingdeePurchaseRequest : kingdeePurchaseRequestList) {
            kingdeePurchaseRequest.setFMaterialId(materialIdToNumberMap.get(kingdeePurchaseRequest.getFMaterialId()));
            ApsPurchaseRequest apsPurchaseRequest = kingdeeToApsPurchaseRequest.convert(kingdeePurchaseRequest, maxVersion);
            apsPurchaseRequestList.add(apsPurchaseRequest);
        }
        return apsPurchaseRequestList;
    }

    private Map<String, String> getMaterialIdToNumberMap() throws Exception {
        QueryParam queryParam;
        queryParam = new QueryParam();
        queryParam.setFormId("BD_MATERIAL");
        queryParam.setFieldKeys("FMaterialId,FNumber");
        List<MaterialIdToName> midToNameList = api.executeBillQuery(queryParam, MaterialIdToName.class);
        Map<String, String> materialIdToNumberMap = new HashMap<>();
        midToNameList.forEach(materialIdToName -> materialIdToNumberMap.put(materialIdToName.getFMaterialId(), materialIdToName.getFNumber()));
        return materialIdToNumberMap;
    }

    private List<KingdeePurchaseRequest> getKingdeePurchaseRequests(Map<String, String> fNameFUserIdMap, Map<String, String> fieldIdToBillTypeIdMap) throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("PUR_Requisition");
        queryParam.setFieldKeys("FMaterialId,FBaseUnitQty,FArrivalDate");

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

    private Map<String, String> getfieldIdToBillTypeIdMap() throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("BOS_BillType");//设置查询参数的表单ID为BD_MATERIAL
        queryParam.setFieldKeys("FBILLTYPEID,FName");
        List<FBILLTYPEIDToName> fidToName = api.executeBillQuery(queryParam, FBILLTYPEIDToName.class);//调用api的executeBillQuery方法进行查询queryParam，并且传入查询参数和目标数据类型MaterialIdToName.class，返回的数据将被转换为MaterialIdToName类型的对象列表，其实是一种映射关系
        Map<String, String> fieldIdToBillTypeIdMap = new HashMap<>();
        fidToName.forEach(c -> {
            fieldIdToBillTypeIdMap.put(c.getFName(), c.getFBILLTYPEID());
        });
        return fieldIdToBillTypeIdMap;
    }

    private Map<String, String> getFNameFuserIdMap() throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("SEC_User");
        queryParam.setFieldKeys("FUserID,FName");
        List<CreateIdToName> crtidToNameList = api.executeBillQuery(queryParam, CreateIdToName.class);
        Map<String, String> fNameFUserIdMap = new HashMap<>();
        crtidToNameList.forEach(createIdToName -> fNameFUserIdMap.put(createIdToName.getFName(), createIdToName.getFUserID()));
        return fNameFUserIdMap;
    }
}




