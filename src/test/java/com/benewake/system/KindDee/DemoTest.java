package com.benewake.system.KindDee;

import com.benewake.system.ApsSystemApplication;
import com.benewake.system.entity.kingdee.*;
import com.benewake.system.entity.enums.FMaterialStatus;
import com.benewake.system.entity.kingdee.transfer.CreateIdToName;
import com.benewake.system.entity.kingdee.transfer.FBILLTYPEIDToName;
import com.benewake.system.entity.kingdee.transfer.FIDToNumber;
import com.benewake.system.entity.kingdee.transfer.MaterialIdToName;
import com.kingdee.bos.webapi.entity.QueryParam;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.util.*;

/**
 * @author Lcs
 * @CreateTime 2023年06月27 17:43
 * @Description TODO
 */
@SpringBootTest
@ContextConfiguration(classes = ApsSystemApplication.class)
public class DemoTest {

    @Autowired
    private K3CloudApi api;
    /*2.6采购申请单列表*/
    @Test
    public void shoprequestData() throws Exception {

        QueryParam queryParam = new QueryParam();
        // 创建人和审核人映射表
        queryParam = new QueryParam();
        queryParam.setFormId("SEC_User");
        queryParam.setFieldKeys("FUserID,FName");
        List<CreateIdToName> crtidToName = api.executeBillQuery(queryParam, CreateIdToName.class);
        Map<String,String> critn = new HashMap<>();
        crtidToName.forEach(c->{
            critn.put(c.getFName(),c.getFUserID());
        });
        crtidToName = null;

        //单据映射表
        queryParam = new QueryParam();
        queryParam.setFormId("BOS_BillType");//设置查询参数的表单ID为BD_MATERIAL
        queryParam.setFieldKeys("FBILLTYPEID,FName");
        List<FBILLTYPEIDToName> fidToName = api.executeBillQuery(queryParam, FBILLTYPEIDToName.class);//调用api的executeBillQuery方法进行查询queryParam，并且传入查询参数和目标数据类型MaterialIdToName.class，返回的数据将被转换为MaterialIdToName类型的对象列表，其实是一种映射关系
        Map<String,String> ftn = new HashMap<>();
        fidToName.forEach(c->{//遍历midtoname列表并且对列表中的数据做括号内的操作
            ftn.put(c.getFName(),c.getFBILLTYPEID());//将物料的id作为键，物料编号作为值，将键值对添加到映射表中
        });


        queryParam.setFormId("PUR_Requisition");
        queryParam.setFieldKeys("FMaterialId,FBaseUnitQty,FArrivalDate");

        List<String> queryFilters = new ArrayList<>();
        queryFilters.add("FBillTypeID = '" + ftn.get("标准采购申请") + "'");
        queryFilters.add("FMRPTerminateStatus = 'A'");
        queryFilters.add("FMRPCloseStatus = 'A'");
        queryFilters.add("FDocumentStatus = 'C'");
        queryFilters.add("FCloseStatus = 'A'");
        queryFilters.add("FCancelStatus = 'A'");
        queryFilters.add("FOrderJoinQty = '0' ");
        queryFilters.add("(FCreatorId = '" + critn.get("王小溪") + "' OR FCreatorId = '" + critn.get("葛婧") + "')");

        for (String filter : queryFilters) {
            System.out.println("Filter: " + filter);
        }

        queryParam.setFilterString(String.join(" and ", queryFilters));

        List<shoprequestDD> result = api.executeBillQuery(queryParam, shoprequestDD.class);
        System.out.println("查询到的数据数量: " + result.size());

        // 物料映射表
        queryParam = new QueryParam();
        queryParam.setFormId("BD_MATERIAL");
        queryParam.setFieldKeys("FMaterialId,FNumber");
        List<MaterialIdToName> midToName = api.executeBillQuery(queryParam, MaterialIdToName.class);
        Map<String,String> mtn = new HashMap<>();
        midToName.forEach(c->{
            mtn.put(c.getFMaterialId(),c.getFNumber());
        });


        for (shoprequestDD m : result) {

            m.setFMaterialId(mtn.get(m.getFMaterialId()));
            System.out.println(m.toString());

        }


    }

    @Test
    public void ProUse() throws Exception {
        //子查询中取出FBillNo 列
        QueryParam subQueryParam = new QueryParam();
        subQueryParam.setFormId("PRD_MO");
        subQueryParam.setFieldKeys("FBillNo");
        subQueryParam.setFilterString("FPickMtrlStatus = '2'");
        List<YourResultClassForSubQuery> subMoQueryResult = api.executeBillQuery(subQueryParam, YourResultClassForSubQuery.class);

        // 从子查询结果中提取 FBillNo 列，存储在列表中
        List<String> subMoBillNos = new ArrayList<>();
        for (YourResultClassForSubQuery subQueryRow : subMoQueryResult) {
            String subMoBillNo = subQueryRow.getFBillNo(); // 获取子查询结果中的数据
            subMoBillNos.add("'" + subMoBillNo + "'"); // 添加到列表，并添加单引号以进行比较
        }

        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("PRD_PPBOM");
        queryParam.setFieldKeys("FMaterialID,FMOBillNO,FMaterialID2,FMaterialType,FMustQty,FPickedQty,FGoodReturnQty,FProcessDefectReturnQty");

        // 条件筛选
        List<String> queryFilters = new ArrayList<>();


        // 使用子查询结果作为条件
        if (!subMoBillNos.isEmpty()) {
            String subMoBillNosCondition = "FMOBillNO in (" + String.join(",", subMoBillNos) + ")";
            queryFilters.add(subMoBillNosCondition);
        }
//        queryFilters.add("FWorkshopID = 102714");
        queryFilters.add("FDocumentStatus = 'C' AND FWorkshopID = 102714 AND (FMoEntryStatus = '3' OR FMoEntryStatus = '4' OR FMoEntryStatus = '5')");     // 单据状态=已审核
//        queryFilters.add("FMoEntryStatus = '3' OR FMoEntryStatus = '4' OR FMoEntryStatus = '5'");


        for (String filter : queryFilters) {
            System.out.println("Filter: " + filter);
        }


        queryParam.setFilterString(String.join(" and ", queryFilters));
//        queryParam.setLimit(100);

        List<KingdeeProductionMaterial> result = api.executeBillQuery(queryParam, KingdeeProductionMaterial.class);
        System.out.println("查询到的数据数量: " + result.size());

        // 物料映射表
        queryParam = new QueryParam();
        queryParam.setFormId("BD_MATERIAL");
        queryParam.setFieldKeys("FMaterialId,FNumber");
        List<MaterialIdToName> midToName = api.executeBillQuery(queryParam, MaterialIdToName.class);
        Map<String, String> mtn = new HashMap<>();
        midToName.forEach(c -> {
            mtn.put(c.getFMaterialId(), c.getFNumber());
        });


        for (KingdeeProductionMaterial m : result) {
            String materialIdName = mtn.get(m.getFMaterialID());
            String materialId2Name = mtn.get(m.getFMaterialID2());

            m.setFMaterialID(materialIdName);
            m.setFMaterialID2(materialId2Name);

            String fMaterialType = m.getFMaterialType();
            String fMaterialTypeDescription = FMaterialStatus.getByCode(fMaterialType).getDescription();
            m.setFMaterialType(fMaterialTypeDescription);

            System.out.println(m);
        }


    }

    @Test
    public void WEIdata() throws Exception {

        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("SUB_SUBREQORDER");
        queryParam.setFieldKeys("FBillNo,FBillType,FMaterialId,FQty,FStatus,FPickMtrlStatus,FStockInQty,FBomId");

        // 条件筛选
        List<String> queryFilters = new ArrayList<>();
        //创建一个空的字符串列表，用于存储查询过滤条件
        queryFilters.add("FDocumentStatus = 'C'");     // 单据状态=已审核
        queryFilters.add("FCancelStatus = 'A'");       // 作废状态=未作废
        queryFilters.add("FStatus != '6'");              // 业务状态≠结案
        queryFilters.add("FStatus != '7'");
        for (String filter : queryFilters) {
            System.out.println("Filter: " + filter);
        }


        queryParam.setFilterString(String.join(" and ", queryFilters));
//        queryParam.setLimit(100);
        List<KingdeeOutsourcedOrder> result = api.executeBillQuery(queryParam, KingdeeOutsourcedOrder.class);
        System.out.println("查询到的数据数量: " + result.size());

        // 物料映射表
        queryParam = new QueryParam();
        queryParam.setFormId("BD_MATERIAL");
        queryParam.setFieldKeys("FMaterialId,FNumber");
        List<MaterialIdToName> midToName = api.executeBillQuery(queryParam, MaterialIdToName.class);
        Map<String, String> mtn = new HashMap<>();
        midToName.forEach(c -> {
            mtn.put(c.getFMaterialId(), c.getFNumber());
        });

        //单据映射表
        queryParam = new QueryParam();
        queryParam.setFormId("BOS_BillType");//设置查询参数的表单ID为BD_MATERIAL
        queryParam.setFieldKeys("FBILLTYPEID,FName");
        List<FBILLTYPEIDToName> fidToName = api.executeBillQuery(queryParam, FBILLTYPEIDToName.class);//调用api的executeBillQuery方法进行查询queryParam，并且传入查询参数和目标数据类型MaterialIdToName.class，返回的数据将被转换为MaterialIdToName类型的对象列表，其实是一种映射关系
        Map<String, String> ftn = new HashMap<>();
        fidToName.forEach(c -> {//遍历midtoname列表并且对列表中的数据做括号内的操作
            ftn.put(c.getFBILLTYPEID(), c.getFName());//将物料的id作为键，物料编号作为值，将键值对添加到映射表中
        });

        //BOM版本号映射表
        queryParam = new QueryParam();
        queryParam.setFormId("ENG_BOM");//设置查询参数的表单ID为BD_MATERIAL
        queryParam.setFieldKeys("FID,FNumber");
        List<FIDToNumber> bidToName = api.executeBillQuery(queryParam, FIDToNumber.class);//调用api的executeBillQuery方法进行查询queryParam，并且传入查询参数和目标数据类型MaterialIdToName.class，返回的数据将被转换为MaterialIdToName类型的对象列表，其实是一种映射关系
        Map<String, String> btn = new HashMap<>();
        bidToName.forEach(c -> {//遍历midtoname列表并且对列表中的数据做括号内的操作
            btn.put(c.getFID(), c.getFNumber());//将物料的id作为键，物料编号作为值，将键值对添加到映射表中
        });

        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("1", "计划");
        statusMap.put("2", "计划确认");
        statusMap.put("3", "下达");
        statusMap.put("5", "完工");
        statusMap.put("6", "结案");
        statusMap.put("7", "结算");

        // 创建 FPickMtrlStatus id 到状态文字的映射 HashMap
        Map<String, String> pickStatusMap = new HashMap<>();
        pickStatusMap.put("1", "未领料");
        pickStatusMap.put("2", "部分领料");
        pickStatusMap.put("3", "全部领料");
        pickStatusMap.put("4", "超额领料");


        for (KingdeeOutsourcedOrder m : result) {
            // 获取 FStatus 的 id
            String statusId = m.getFStatus();
            // 使用映射 HashMap 获取状态文字
            String statusText = statusMap.get(statusId);
            if (statusText != null) {
                m.setFStatus(statusText);
            } else {
                m.setFStatus("未知状态"); // 或者其他默认值
            }
            // 获取 FPickMtrlStatus 的 id
            String pickStatusId = m.getFPickMtrlStatus();
            // 使用映射 HashMap 获取状态文字
            String pickStatusText = pickStatusMap.get(pickStatusId);
            if (pickStatusText != null) {
                m.setFPickMtrlStatus(pickStatusText);
            } else {
                m.setFPickMtrlStatus("未知状态"); // 或者其他默认值
            }
            m.setFMaterialId(mtn.get(m.getFMaterialId()));
            m.setFBomId(btn.get(m.getFBomId()));
            String originalFBillType = m.getFBillType();
            m.setFBillType(ftn.get(originalFBillType));
            System.out.println(m);
        }
    }

    @Test
    public void productData() throws Exception {

        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("PRD_MO");
        queryParam.setFieldKeys("FStatus,FWorkshopID,FBillNo,FBillType,FMaterialId,FQty,FStatus,FPickMtrlStatus,FStockInQuaAuxQty,FBomId,FWorkShopID");

        // 条件筛选
        List<String> queryFilters = new ArrayList<>();
        //创建一个空的字符串列表，用于存储查询过滤条件
        queryFilters.add("FDocumentStatus = 'C'");     // 单据状态=已审核
        queryFilters.add("FCancelStatus = 'A'");       // 作废状态=未作废
        queryFilters.add("FStatus != '6'");              // 业务状态≠结案
        queryFilters.add("FStatus != '7'");
        queryFilters.add("FWorkshopID =102714");

        for (String filter : queryFilters) {
            System.out.println("Filter: " + filter);
        }

        queryParam.setFilterString(String.join(" and ", queryFilters));

        List<KingdeeProductionOrder> result = api.executeBillQuery(queryParam, KingdeeProductionOrder.class);
        System.out.println("查询到的数据数量: " + result.size());

        // 物料映射表
        queryParam = new QueryParam();
        queryParam.setFormId("BD_MATERIAL");
        queryParam.setFieldKeys("FMaterialId,FNumber");
        List<MaterialIdToName> midToName = api.executeBillQuery(queryParam, MaterialIdToName.class);
        Map<String, String> mtn = new HashMap<>();
        midToName.forEach(c -> {
            mtn.put(c.getFMaterialId(), c.getFNumber());
        });

        //单据映射表
        queryParam = new QueryParam();
        queryParam.setFormId("BOS_BillType");//设置查询参数的表单ID为BD_MATERIAL
        queryParam.setFieldKeys("FBILLTYPEID,FName");
        List<FBILLTYPEIDToName> fidToName = api.executeBillQuery(queryParam, FBILLTYPEIDToName.class);//调用api的executeBillQuery方法进行查询queryParam，并且传入查询参数和目标数据类型MaterialIdToName.class，返回的数据将被转换为MaterialIdToName类型的对象列表，其实是一种映射关系
        Map<String, String> ftn = new HashMap<>();
        fidToName.forEach(c -> {//遍历midtoname列表并且对列表中的数据做括号内的操作
            ftn.put(c.getFBILLTYPEID(), c.getFName());//将物料的id作为键，物料编号作为值，将键值对添加到映射表中
        });

        //BOM版本号映射表
        queryParam = new QueryParam();
        queryParam.setFormId("ENG_BOM");//设置查询参数的表单ID为BD_MATERIAL
        queryParam.setFieldKeys("FID,FNumber");
        List<FIDToNumber> bidToName = api.executeBillQuery(queryParam, FIDToNumber.class);//调用api的executeBillQuery方法进行查询queryParam，并且传入查询参数和目标数据类型MaterialIdToName.class，返回的数据将被转换为MaterialIdToName类型的对象列表，其实是一种映射关系
        Map<String, String> btn = new HashMap<>();
        bidToName.forEach(c -> {//遍历midtoname列表并且对列表中的数据做括号内的操作
            btn.put(c.getFID(), c.getFNumber());//将物料的id作为键，物料编号作为值，将键值对添加到映射表中
        });

        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("1", "计划");
        statusMap.put("2", "计划确认");
        statusMap.put("3", "下达");
        statusMap.put("5", "完工");
        statusMap.put("6", "结案");
        statusMap.put("7", "结算");

        // 创建 FPickMtrlStatus id 到状态文字的映射 HashMap
        Map<String, String> pickStatusMap = new HashMap<>();
        pickStatusMap.put("1", "未领料");
        pickStatusMap.put("2", "部分领料");
        pickStatusMap.put("3", "全部领料");
        pickStatusMap.put("4", "超额领料");

        for (KingdeeProductionOrder m : result) {
            // 获取 FStatus 的 id
            String statusId = m.getFStatus();

            // 使用映射 HashMap 获取状态文字
            String statusText = statusMap.get(statusId);
            if (statusText != null) {
                m.setFStatus(statusText);
            }

            // 获取 FPickMtrlStatus 的 id
            String pickStatusId = m.getFPickMtrlStatus();

            // 使用映射 HashMap 获取状态文字
            String pickStatusText = pickStatusMap.get(pickStatusId);
            if (pickStatusText != null) {
                m.setFPickMtrlStatus(pickStatusText);
            }

            m.setFMaterialId(mtn.get(m.getFMaterialId()));
            String originalFBillType = m.getFBillType();
            m.setFBillType(ftn.get(originalFBillType));
            m.setFBomId(btn.get(m.getFBomId()));
            System.out.println(m);
        }

    }

}
