package com.benewake.system.service.scheduling.kingdee;

import com.benewake.system.entity.ApsOrder;
import com.benewake.system.entity.ApsPurchaseRequestsOrders;
import com.benewake.system.entity.kingdee.transfer.CreateIdToName;
import com.benewake.system.entity.kingdee.transfer.FBILLTYPEIDToName;
import com.benewake.system.entity.kingdee.transfer.MaterialIdToName;
import com.kingdee.bos.webapi.entity.QueryParam;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public abstract class ApsPurchaseRequestsOrdersBaseService {


    @Autowired
    private K3CloudApi api;

    public abstract String getServiceName();

    public abstract List<ApsPurchaseRequestsOrders> getKingdeeDates() throws Exception;

    protected Map<String, String> getFNameFuserIdMap() throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("SEC_User");
        queryParam.setFieldKeys("FUserID,FName");
        List<CreateIdToName> crtidToNameList = api.executeBillQuery(queryParam, CreateIdToName.class);
        Map<String, String> fNameFUserIdMap = new HashMap<>();
        crtidToNameList.forEach(createIdToName -> fNameFUserIdMap.put(createIdToName.getFName(), createIdToName.getFUserID()));
        return fNameFUserIdMap;
    }

    protected Map<String, String> getfieldIdToBillTypeIdMap() throws Exception {
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

    protected Map<String, String> getMaterialIdToNumberMap() throws Exception {
        QueryParam queryParam;
        queryParam = new QueryParam();
        queryParam.setFormId("BD_MATERIAL");
        queryParam.setFieldKeys("FMaterialId,FNumber");
        List<MaterialIdToName> midToNameList = api.executeBillQuery(queryParam, MaterialIdToName.class);
        Map<String, String> materialIdToNumberMap = new HashMap<>();
        midToNameList.forEach(materialIdToName -> materialIdToNumberMap.put(materialIdToName.getFMaterialId(), materialIdToName.getFNumber()));
        return materialIdToNumberMap;
    }
}
