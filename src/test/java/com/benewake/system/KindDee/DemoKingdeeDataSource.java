package com.benewake.system.KindDee;

import com.benewake.system.ApsSystemApplication;
import com.benewake.system.entity.*;
import com.benewake.system.entity.enums.BOMChangeType;
import com.benewake.system.entity.kingdee.*;
import com.benewake.system.entity.enums.FMaterialStatus;
import com.benewake.system.entity.kingdee.transfer.*;
import com.benewake.system.redis.DistributedLock;
import com.benewake.system.service.*;
import com.benewake.system.service.scheduling.kingdee.ApsMaterialNameMappingService;
import com.benewake.system.service.scheduling.kingdee.ApsImmediatelyInventoryService;
import com.benewake.system.service.scheduling.kingdee.ApsProductionMaterialService;
import com.benewake.system.service.scheduling.result.ApsAllPlanNumInProcessService;
import com.benewake.system.transfer.KingdeeToApsProductionMaterial;
import com.kingdee.bos.webapi.entity.QueryParam;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @author Lcs
 * @CreateTime 2023年06月27 17:43
 * @Description TOD
 */
@SpringBootTest
@Slf4j
@ContextConfiguration(classes = ApsSystemApplication.class)
public class DemoKingdeeDataSource {

    @Autowired
    private K3CloudApi api;

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Autowired
    private DistributedLock distributedLock;

    @Autowired
    private ApsImmediatelyInventoryService apsImmediatelyInventoryService;

    @Autowired
    private Map<String, ApsIntfaceDataServiceBase> apsIntfaceDataServiceBase;

    @Autowired
    private ApsTableVersionService apsTableVersionService;

    @Autowired
    private ApsProcessCapacityService apsProcessCapacityService;

    @Autowired
    private ApsProcessNamePoolService apsProcessNamePoolService;
    @Autowired
    private ApsAllPlanNumInProcessService apsAllPlanNumInProcessService;

    @Autowired
    RedissonClient redissonClient;

    @org.junit.jupiter.api.Test
    public void testdata() throws Exception {
        RLock lock = redissonClient.getLock("123");
        lock.tryLock(20, TimeUnit.SECONDS);
        Thread.sleep(100000);
    }


    public static List<ApsAllPlanNumInProcess> sortEntities(List<ApsAllPlanNumInProcess> entities) {
        return entities.stream()
                .sorted((entity1, entity2) -> {
                    // 首先按task_date升序排序
                    int dateComparison = convertDate(entity1.getTaskDate()).compareTo(convertDate(entity2.getTaskDate()));
                    if (dateComparison != 0) {
                        return dateComparison;
                    }

                    // 如果task_date相等，按start_time降序排序
                    int timeComparison = convertTime(entity2.getStartTime()).compareTo(convertTime(entity1.getStartTime()));
                    if (timeComparison != 0) {
                        return timeComparison;
                    }

                    // 如果start_time也相等，按end_time升序排序
                    return convertTime(entity1.getEndTime()).compareTo(convertTime(entity2.getEndTime()));
                })
                .collect(Collectors.toList());
    }

    private static String convertDate(String date) {
        // 将日期格式转换为排序友好的格式，例如：11-23 -> 1123
        return date.replace("-", "");
    }

    private static String convertTime(String time) {
        // 将时间格式转换为排序友好的格式，例如：9:0 -> 0900
        String[] parts = time.split(":");
        String hour = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
        String minute = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
        return hour + minute;
    }

//    public ApsMaterialBom GET(FMATERIALIDCHILD) {
//        QueryParam queryParam = new QueryParam();
//        queryParam.setFormId("ENG_BOM");
//        queryParam.setFieldKeys("FNumber,FUseOrgId,FMATERIALID,FDocumentStatus,FMATERIALIDCHILD,FNUMERATOR,FDENOMINATOR,FFIXSCRAPQTYLOT,FSCRAPRATE,FForbidStatus,FEXPIREDATE,FReplaceGroup");
//
//        List<String> queryFilters = new ArrayList<>();
//
//        queryFilters.add("FUseOrgId = '1'");
//        queryFilters.add("FForbidStatus = 'A'");
//        queryFilters.add("FMATERIALIDCHILD = " + FMATERIALIDCHILD);
//        // 使用 Java 中的日期类获取当前日期，并将其格式化为字符串
//        Date currentDate = new Date();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String currentDateStr = dateFormat.format(currentDate);
//        queryFilters.add("TO_DATE(FEXPIREDATE, 'yyyy-MM-dd') >= TO_DATE('" + currentDateStr + "', 'yyyy-MM-dd')");
////        String subquery = "(SELECT MAX(FNumber) FROM ENG_BOM)";
////        queryFilters.add("FNumber = '" + subquery + "'");
//
//
//        for (String filter : queryFilters) {
//            System.out.println("Filter: " + filter);
//        }
//
//        queryParam.setFilterString(String.join(" and ", queryFilters));
//
//        List<WuliaoDD> result = api.executeBillQuery(queryParam, WuliaoDD.class);
//    }

    @Autowired
    private ApsMaterialNameMappingService apsMaterialNameMappingService;

    @org.junit.jupiter.api.Test
    public void testdat123a() throws Exception {
        List<String> queryFilters = new ArrayList<>();
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("ENG_BOM");
        queryParam.setFieldKeys("FMATERIALID,FITEMNAME,FITEMMODEL,FMATERIALIDCHILD,FCHILDITEMNAME,FCHILDITEMMODEL");
        queryParam.setFilterString(String.join(" and ", queryFilters));
        List<KingdeeDataSource> kingdeeDataSources = api.executeBillQuery(queryParam, KingdeeDataSource.class);

        queryParam = new QueryParam();
        queryParam.setFormId("BD_MATERIAL");
        queryParam.setFieldKeys("FMaterialId,FNumber");
        List<MaterialIdToName> materialIdToNames = api.executeBillQuery(queryParam, MaterialIdToName.class);
        Map<String, String> fMatIdToFnumberMap = new HashMap<>();
        materialIdToNames.forEach(materialIdToName -> {
            fMatIdToFnumberMap.put(materialIdToName.getFMaterialId(), materialIdToName.getFNumber());
        });

        List<ApsMaterialNameMapping> apsMaterialNameMappings = new ArrayList<>();
        for (KingdeeDataSource kingdeeDataSource : kingdeeDataSources) {
            getApsDataSources(fMatIdToFnumberMap, apsMaterialNameMappings, kingdeeDataSource);
            System.out.println(kingdeeDataSource);
        }
        apsMaterialNameMappings = apsMaterialNameMappings.stream().distinct().collect(Collectors.toList());
    }

    private void getApsDataSources(Map<String, String> fMatIdToFnumberMap, List<ApsMaterialNameMapping> apsMaterialNameMappings, KingdeeDataSource kingdeeDataSource) {
        String fNumber = fMatIdToFnumberMap.get(kingdeeDataSource.getFMaterialId());
        kingdeeDataSource.setFMaterialId(fNumber);
        ApsMaterialNameMapping apsMaterialNameMapping = new ApsMaterialNameMapping();
        apsMaterialNameMapping.setFMaterialId(kingdeeDataSource.getFMaterialId());
        apsMaterialNameMapping.setFMaterialName(kingdeeDataSource.getFItemName());
        apsMaterialNameMapping.setFItemModel(kingdeeDataSource.getFItemModel());
        apsMaterialNameMappings.add(apsMaterialNameMapping);
        fNumber = fMatIdToFnumberMap.get(kingdeeDataSource.getFMaterialIdChild());
        kingdeeDataSource.setFMaterialIdChild(fNumber);
        apsMaterialNameMapping = new ApsMaterialNameMapping();
        apsMaterialNameMapping.setFMaterialId(kingdeeDataSource.getFMaterialIdChild());
        apsMaterialNameMapping.setFMaterialName(kingdeeDataSource.getFChildItemName());
        apsMaterialNameMapping.setFItemModel(kingdeeDataSource.getFChildItemModel());
        apsMaterialNameMappings.add(apsMaterialNameMapping);
    }


    @org.junit.jupiter.api.Test
    public void TestMaptestdata() throws Exception {
        List<String> queryFilters = new ArrayList<>();
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("ENG_ECNOrder");
        queryParam.setFieldKeys("FApproveDate,FChangeLabel,FBOMVERSION,FParentMaterialId,FMATERIALIDCHILD,FCHILDITEMNAME");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
//        queryFilters.add("FApproveDate <= '" + sdf.format(new Date()) + "'");
        String date = "2023-10-01T09:34:31.117";
        queryFilters.add("FApproveDate > '" + date + "'");
        System.out.println(sdf.format(new Date()));
        queryFilters.add("FDocumentStatus = 'C'");     // 单据状态=已审核
        queryFilters.add("FCancelStatus = 'A'");       // 作废状态=未作废
        queryParam.setFilterString(String.join(" and ", queryFilters));
        List<MaterialBomChange> result = api.executeBillQuery(queryParam, MaterialBomChange.class);

        queryParam = new QueryParam();
        queryParam.setFormId("BD_MATERIAL");
        queryParam.setFieldKeys("FMaterialId,FNumber");
        List<MaterialIdToName> midToName = api.executeBillQuery(queryParam, MaterialIdToName.class);
        Map<String, String> mtn = new HashMap<>();
        midToName.forEach(c -> {
            mtn.put(c.getFMaterialId(), c.getFNumber());
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
        List<MaterialBomChange> addList = new ArrayList<>(); // 用于存储要添加的记录
        List<MaterialBomChange> deleteList = new ArrayList<>(); // 用于存储要删除的记录
        for (MaterialBomChange m : result) {
            String ubMatIdToNums1 = mtn.get(m.getFParentMaterialId());
            m.setFParentMaterialId(ubMatIdToNums1);
            String ubMatIdToNums2 = btn.get(m.getFBomVersion());
            m.setFBomVersion(ubMatIdToNums2);
//            String ubMatIdToNums3 = mtn.get(m.getFMATERIALIDCHILD());
//            m.setFMATERIALIDCHILD(ubMatIdToNums3);
            if (Objects.equals(m.getFChangeLabel(), BOMChangeType.CHANGE_BEFORE.getCode())) {
                deleteList.add(m);
            } else if (Objects.equals(m.getFChangeLabel(), BOMChangeType.CHANGE_AFTER.getCode())) {
                addList.add(m);
            } else if (Objects.equals(m.getFChangeLabel(), BOMChangeType.ADD.getCode())) {
                addList.add(m);
            } else if (Objects.equals(m.getFChangeLabel(), BOMChangeType.DELETE.getCode())) {
                deleteList.add(m);
            }
            System.out.println(m);
        }
    }

    @org.junit.jupiter.api.Test
    public void GET() throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("ENG_BOM");
        queryParam.setFieldKeys("FNumber,FUseOrgId,FMATERIALID,FDocumentStatus,FMATERIALIDCHILD,FNUMERATOR,FDENOMINATOR,FFIXSCRAPQTYLOT,FSCRAPRATE,FForbidStatus,FEXPIREDATE,FReplaceGroup");
        List<String> queryFilters = new ArrayList<>();
        queryFilters.add("FUseOrgId = '1'");
        queryFilters.add("FForbidStatus = 'A'");
        queryFilters.add("FMATERIALIDCHILD = " + "594638");
        // 使用 Java 中的日期类获取当前日期，并将其格式化为字符串
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateStr = dateFormat.format(currentDate);
        queryFilters.add("TO_DATE(FEXPIREDATE, 'yyyy-MM-dd') >= TO_DATE('" + currentDateStr + "', 'yyyy-MM-dd')");
        for (String filter : queryFilters) {
            System.out.println("Filter: " + filter);
        }
        queryParam.setFilterString(String.join(" and ", queryFilters));
        List<KingdeeMaterialBom> result = api.executeBillQuery(queryParam, KingdeeMaterialBom.class);
        System.out.println(result);
    }

    //工序名称 匹配id
    @org.junit.jupiter.api.Test
    public void testDa1ta() throws InterruptedException {
//        List<ApsProcessNamePool> apsProcessNamePools = apsProcessNamePoolService.getBaseMapper().selectList(null);
//        Map<String, Integer> collect = apsProcessNamePools.stream()
//                .collect(Collectors.toMap(ApsProcessNamePool::getProcessName, ApsProcessNamePool::getId));
//        List<ApsProcessCapacity> apsProcessCapacities = apsProcessCapacityService.getBaseMapper().selectList(null);
//        apsProcessCapacities.forEach(x-> {
//            String processName = x.getProcessName();
//            Integer integer = collect.get(processName);
//            x.setProcessId(integer);
//        });
//        apsProcessCapacityService.updateBatchById(apsProcessCapacities);
//        Thread.sleep(10000);
    }


    /*2.7采购订单列表
     * 目前数据有问题*/
    private List<YourResultClassForSubQuery> getYourResultClassForSubQueries() throws Exception {
        //子查询中取出FBillNo 列
        QueryParam subQueryParam = new QueryParam();
        subQueryParam.setFormId("PRD_MO");
        subQueryParam.setFieldKeys("FBillNo");
        subQueryParam.setFilterString("FPickMtrlStatus = '2'");
        List<YourResultClassForSubQuery> subMoQueryResult = api.executeBillQuery(subQueryParam, YourResultClassForSubQuery.class);
        return subMoQueryResult;
    }

    @Autowired
    private ApsProductionMaterialService apsProductionMaterialService;

    @org.junit.jupiter.api.Test
    public void testDat1a() throws Exception {
        List<YourResultClassForSubQuery> subMoQueryResult = getYourResultClassForSubQueries();
        List<String> subMoBillNos = new ArrayList<>();
        for (YourResultClassForSubQuery subQueryRow : subMoQueryResult) {
            String subMoBillNo = subQueryRow.getFBillNo(); // 获取子查询结果中的数据
            subMoBillNos.add("'" + subMoBillNo + "'"); // 添加到列表，并添加单引号以进行比较
        }

        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("PRD_PPBOM");
        queryParam.setFieldKeys("FMaterialID,FMaterialName,FMOBillNO,FMaterialID2,FMaterialName1,FMaterialType,FMustQty,FPickedQty,FGoodReturnQty,FProcessDefectReturnQty");

        // 条件筛选
        List<String> queryFilters = new ArrayList<>();

        // 使用子查询结果作为条件
        if (!subMoBillNos.isEmpty()) {
            String subMoBillNosCondition = "FMOBillNO in (" + String.join(",", subMoBillNos) + ")";
            queryFilters.add(subMoBillNosCondition);
        }
        queryFilters.add("FDocumentStatus = 'C' AND FWorkshopID = 102714 AND (FMoEntryStatus = '3' OR FMoEntryStatus = '4' OR FMoEntryStatus = '5')");     // 单据状态=已审核

        queryParam.setFilterString(String.join(" and ", queryFilters));
//        queryParam.setLimit(100);

        List<KingdeeProductionMaterial> result = api.executeBillQuery(queryParam, KingdeeProductionMaterial.class);
//        int a = 1;

        // 物料映射表
        Map<String, String> mtn = getMaterialIdToNameMap();
        //转换
        ArrayList<ApsProductionMaterial> apsProductionMaterials = getApsProductionMaterials(result, mtn);
        boolean b = apsProductionMaterialService.saveBatch(apsProductionMaterials);
        System.out.println(b);
    }

    @Autowired
    private KingdeeToApsProductionMaterial kingdeeToApsProductionMaterial;

    private ArrayList<ApsProductionMaterial> getApsProductionMaterials(List<KingdeeProductionMaterial> result, Map<String, String> mtn) throws NoSuchFieldException, IllegalAccessException {
        ArrayList<ApsProductionMaterial> apsProductionMaterials = new ArrayList<>();
        Integer maxVersion = 1;
        for (KingdeeProductionMaterial kingdeeProductionMaterial : result) {
            String materialIdName = mtn.get(kingdeeProductionMaterial.getFMaterialID());
            String materialId2Name = mtn.get(kingdeeProductionMaterial.getFMaterialID2());

            kingdeeProductionMaterial.setFMaterialID(materialIdName);
            kingdeeProductionMaterial.setFMaterialID2(materialId2Name);

            String fMaterialType = kingdeeProductionMaterial.getFMaterialType();
            String fMaterialTypeDescription = FMaterialStatus.getByCode(fMaterialType).getDescription();
            kingdeeProductionMaterial.setFMaterialType(fMaterialTypeDescription);
            ApsProductionMaterial apsProductionMaterial = kingdeeToApsProductionMaterial.convert(kingdeeProductionMaterial, maxVersion);
            apsProductionMaterials.add(apsProductionMaterial);
        }
        return apsProductionMaterials;
    }

    private Map<String, String> getMaterialIdToNameMap() throws Exception {
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

    @org.junit.jupiter.api.Test
    public void fetchWuliaoData() throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("ENG_BOM");
        queryParam.setFieldKeys("FNumber,FUseOrgId,FMaterialId,FDocumentStatus,FMaterialIDChild,FNumerator,FDenominator,FFixScrapQtyLot,FScrapRate,FForBidStatus,FExpireDate");

        List<String> queryFilters = new ArrayList<>();
        queryFilters.add("FUseOrgId = '1'");
        queryFilters.add("FForbidStatus = 'A'");
        // 使用 Java 中的日期类获取当前日期，并将其格式化为字符串
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateStr = dateFormat.format(currentDate);
        queryFilters.add("TO_DATE(FEXPIREDATE, 'yyyy-MM-dd') >= TO_DATE('" + currentDateStr + "', 'yyyy-MM-dd')");

        queryParam.setFilterString(String.join(" and ", queryFilters));
        List<KingdeeMaterialBom> result = api.executeBillQuery(queryParam, KingdeeMaterialBom.class);
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
        Map<String, String> docStatusMap = new HashMap<>();
        docStatusMap.put("Z", "暂存");
        docStatusMap.put("A", "创建");
        docStatusMap.put("B", "审核中");
        docStatusMap.put("C", "已审核");
        docStatusMap.put("D", "重新审核");
        // 创建一个映射，用于存储每个FMATERIALID对应的最大FNumber
        Map<String, String> maxFNumberMap = new HashMap<>();
        // 创建一个集合，用于去重
        Set<String> processedMaterialIds = new HashSet<>();
        // 创建一个列表，用于存储最大FNumber
        List<String> maxFNumbersList = new ArrayList<>();
        // 遍历result，找到每个不同的FMATERIALID对应的最大FNumber
        for (KingdeeMaterialBom wuliao : result) {
            String materialId = mtn.get(wuliao.getFMaterialID());
            String fNumber = wuliao.getFNumber();
            // 检查是否已经有最大FNumber记录
            if (maxFNumberMap.containsKey(materialId)) {
                String currentMaxFNumber = maxFNumberMap.get(materialId);
                // 比较当前FNumber和已存储的最大FNumber，更新为较大的值
                if (fNumber.compareTo(currentMaxFNumber) > 0) {
                    maxFNumberMap.put(materialId, fNumber);
                    // 更新最大FNumber列表中对应的值
                    maxFNumbersList.remove(currentMaxFNumber);
                    maxFNumbersList.add(fNumber);
                }
            } else {
                // 如果尚未有记录，将当前FNumber作为最大FNumber
                maxFNumberMap.put(materialId, fNumber);
                // 将最大FNumber添加到列表中
                maxFNumbersList.add(fNumber);
            }
        }

        // 再次遍历result，根据最大FNumber打印出相应的记录
        for (KingdeeMaterialBom wuliao : result) {
            String fNumber = wuliao.getFNumber();
            // 获取 FDocumentStatus 的 id
            String docStatusId = wuliao.getFDocumentStatus();
            // 使用映射 HashMap 获取状态文字
            String docStatusText = docStatusMap.get(docStatusId);
            if (docStatusText != null) {
                wuliao.setFDocumentStatus(docStatusText);
            }
            wuliao.setFMaterialID(mtn.get(wuliao.getFMaterialID()));
            wuliao.setFMaterialIDChild(mtn.get(wuliao.getFMaterialIDChild()));
            if (maxFNumbersList.contains(fNumber)) {
                // 打印整条记录
                System.out.println(wuliao);
            }
        }


    }

    @org.junit.jupiter.api.Test
    public void testData() throws Exception {

        QueryParam queryParam;

        // 创建人和审核人映射表
        queryParam = new QueryParam();
        queryParam.setFormId("SEC_User");
        queryParam.setFieldKeys("FUserID,FName");
        List<CreateIdToName> crtidToName = api.executeBillQuery(queryParam, CreateIdToName.class);
        Map<String, String> critn = new HashMap<>();
        crtidToName.forEach(c -> {
            critn.put(c.getFName(), c.getFUserID());
        });

        queryParam.setFormId("PUR_PurchaseOrder");
        queryParam.setFieldKeys("FBillNo,FMaterialId,FRemainReceiveQty,FDeliveryDate");

        // 条件筛选
        List<String> queryFilters = new ArrayList<>();
        //创建一个空的字符串列表，用于存储查询过滤条件
        queryFilters.add("FPurchaseOrgId= 1"); // 采购组织=北醒(北京)光子科技有限公司
        queryFilters.add("FMRPTerminateStatus = 'A'"); // 业务终止=正常
        queryFilters.add("FMRPCloseStatus = 'A'");     // 业务关闭=正常
        queryFilters.add("FMRPFreezeStatus = 'A'");    // 业务冻结=正常
        queryFilters.add("FCloseStatus = 'A'");        // 关闭状态=未关闭
        queryFilters.add("FSupplierId != 336724");      // 供应商≠北京北醒智能设备有限公司
        queryFilters.add("FCancelStatus = 'A'");       // 作废状态=未作废
        queryFilters.add("FDate > dateAdd(day, -365, getdate())");
        queryFilters.add("FCreatorId != '" + critn.get("刘赛") + "'");
        queryFilters.add("FCreatorId != '" + critn.get("宋雨朦") + "'");
        queryFilters.add("FCreatorId != '" + critn.get("张月") + "'");

        for (String filter : queryFilters) {
            System.out.println("Filter: " + filter);
        }

        queryParam.setFilterString(String.join(" and ", queryFilters));
        List<KingdeePurchaseOrder> result = api.executeBillQuery(queryParam, KingdeePurchaseOrder.class);
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
        for (KingdeePurchaseOrder m : result) {
            // 信息替换
            m.setFMaterialId(mtn.get(m.getFMaterialId()));
            System.out.println(m);
        }
    }

    /*2.1即时库存清单*/
    @org.junit.jupiter.api.Test
    public void JiShidata() throws Exception {

        QueryParam queryParam1 = new QueryParam();
        queryParam1.setFormId("STK_LockStock");
        queryParam1.setFieldKeys("FMaterialId,FEXPIRYDATE,FLockQty,FLot");

        List<KingdeeInventoryLock> locks = api.executeBillQuery(queryParam1, KingdeeInventoryLock.class);

        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("STK_Inventory");
        queryParam.setFieldKeys("FMaterialId,FStockName,FBaseQty,FAVBQty,FLot,FExpiryDate");

        List<String> excludedStockNames = Arrays.asList("不良品仓", "销售仓-SZ", "销售仓-北醒", "原材料仓BW", "X产品仓", "返品售后仓", "原材料仓-永新", "半成品仓-永新", "产成品仓-永新", "不良品仓-永新");


        // 条件筛选
        List<String> queryFilters = new ArrayList<>();
        //创建一个空的字符串列表，用于存储查询过滤条件
        queryFilters.add("FStockOrgId = 1");     // 库存组织==北醒(北京)光子科技有限公司
        queryFilters.add("FStockStatusId = 10000");       // 库存状态==可用
        queryFilters.add("FBaseQty != 0");
        for (String stockName : excludedStockNames) {
            queryFilters.add("FStockName != '" + stockName + "'");
        }

        for (String filter : queryFilters) {
            System.out.println("Filter: " + filter);
        }


        queryParam.setFilterString(String.join(" and ", queryFilters));
        List<KingdeeImmediatelyInventory> result = api.executeBillQuery(queryParam, KingdeeImmediatelyInventory.class);
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

            // 更新 FAVBQty 字段
            kingdeeImmediatelyInventory.setFAVBQty(avbQty);
        }

        for (KingdeeImmediatelyInventory kingdeeImmediatelyInventory : result) {
            String materialId = kingdeeImmediatelyInventory.getFMaterialId(); // 获取物料ID

            if (mtn.containsKey(materialId)) {
                String materialNumber = mtn.get(materialId); // 获取物料编号
                kingdeeImmediatelyInventory.setFMaterialId(materialNumber); // 更新物料ID为物料编号
            }

            System.out.println(kingdeeImmediatelyInventory);
        }


//        File excelFile = new File("F:/即时库存8.xlsx");
//        System.out.println(CommonUtils.writeExcel(excelFile, result, JiShi.class));

    }


//    @Test
//    public void shoprequestData1() throws Exception {
//        long l = System.currentTimeMillis();
//        for (ApsIntfaceDataServiceBase service : apsIntfaceDataServiceBase) {
//////            Class<?> aClass = AopProxyUtils.ultimateTargetClass(service);
//            Class<? extends ApsIntfaceDataServiceBase> aClass = service.getClass();
//            String fullClassName = aClass.getName();
//            String simpleName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1, fullClassName.indexOf("$$"));
//
//            System.out.println(toLowerCaseFirstLetter(simpleName));
////            System.out.println(aClass.getSimpleName());
//        }
//        long l1 = System.currentTimeMillis();
//        System.out.println(l1 - l);
//    }

    public static String toLowerCaseFirstLetter(String className) {
        if (className == null || className.isEmpty()) {
            return className;
        }
        char firstChar = Character.toLowerCase(className.charAt(0));
        return firstChar + className.substring(1);
    }

    @org.junit.jupiter.api.Test
    public void shoprequestData() throws Exception {

        QueryParam queryParam;
        // 创建人和审核人映射表
        queryParam = new QueryParam();
        queryParam.setFormId("SEC_User");
        queryParam.setFieldKeys("FUserID,FName");
        List<CreateIdToName> crtidToName = api.executeBillQuery(queryParam, CreateIdToName.class);
        Map<String, String> critn = new HashMap<>();
        crtidToName.forEach(c -> {
            critn.put(c.getFName(), c.getFUserID());
        });

        //单据映射表
        queryParam = new QueryParam();
        queryParam.setFormId("BOS_BillType");//设置查询参数的表单ID为BD_MATERIAL
        queryParam.setFieldKeys("FBILLTYPEID,FName");
        List<FBILLTYPEIDToName> fidToName = api.executeBillQuery(queryParam, FBILLTYPEIDToName.class);//调用api的executeBillQuery方法进行查询queryParam，并且传入查询参数和目标数据类型MaterialIdToName.class，返回的数据将被转换为MaterialIdToName类型的对象列表，其实是一种映射关系
        Map<String, String> ftn = new HashMap<>();
        fidToName.forEach(c -> {//遍历midtoname列表并且对列表中的数据做括号内的操作
            ftn.put(c.getFName(), c.getFBILLTYPEID());//将物料的id作为键，物料编号作为值，将键值对添加到映射表中
        });

        queryParam = new QueryParam();
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

        List<KingdeePurchaseRequest> result = api.executeBillQuery(queryParam, KingdeePurchaseRequest.class);
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


        for (KingdeePurchaseRequest m : result) {

            m.setFMaterialId(mtn.get(m.getFMaterialId()));
            System.out.println(m);

        }


    }

    @org.junit.jupiter.api.Test
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

    @org.junit.jupiter.api.Test
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


    /*2.9库存锁库列表*/
    @org.junit.jupiter.api.Test
    public void LockData() throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("STK_LockStock");
        queryParam.setFieldKeys("FMaterialId,FEXPIRYDATE,FLockQty");

        List<String> queryFilters = new ArrayList<>();
        queryFilters.add("FStockOrgId = 1");

        for (String filter : queryFilters) {
            System.out.println("Filter: " + filter);
        }

        queryParam.setFilterString(String.join(" and ", queryFilters));

        List<KingdeeInventoryLock> result = api.executeBillQuery(queryParam, KingdeeInventoryLock.class);
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

        for (KingdeeInventoryLock m : result) {
            // 获取 FDocumentStatus 的 id
            m.setFMaterialId(mtn.get(m.getFMaterialId()));
            System.out.println(m);
        }

    }

    @org.junit.jupiter.api.Test
    public void getmessagetData() throws Exception {

        QueryParam queryParam = new QueryParam();
        // 创建人和审核人映射表
        queryParam = new QueryParam();
        queryParam.setFormId("SEC_User");
        queryParam.setFieldKeys("FUserID,FName");
        List<CreateIdToName> crtidToName = api.executeBillQuery(queryParam, CreateIdToName.class);
        Map<String, String> critn = new HashMap<>();
        crtidToName.forEach(c -> {
            critn.put(c.getFName(), c.getFUserID());
        });

        queryParam.setFormId("PUR_ReceiveBill");
        queryParam.setFieldKeys("FBillNo,FMaterialId,FMustQty,FCheckQty,FReceiveQty,FCsnReceiveBaseQty,FInStockQty");

        List<String> queryFilters = new ArrayList<>();
        queryFilters.add("FDocumentStatus = 'C'");    // 单据状态=已审核
        queryFilters.add("FENTRYSTATUS = 'A'");          // 行状态=正常
        queryFilters.add("FCancelStatus = 'A'");      // 作废状态=未作废
        queryFilters.add("FPurOrgId   = 1");
        queryFilters.add("FCreatorId != '" + critn.get("刘赛") + "'");
        queryFilters.add("FCreatorId != '" + critn.get("张月") + "'");
        queryFilters.add("FCreatorId != '" + critn.get("杨阳") + "'");
        queryFilters.add("FCreatorId != '" + critn.get("郑杰") + "'");
        queryFilters.add("FCreatorId != '" + critn.get("宋雨朦") + "'");

        for (String filter : queryFilters) {
            System.out.println("Filter: " + filter);
        }


        queryParam.setFilterString(String.join(" and ", queryFilters));

        List<KingdeeReceiveNotice> result = api.executeBillQuery(queryParam, KingdeeReceiveNotice.class);
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


        for (KingdeeReceiveNotice kingdeeReceiveNotice : result) {

            // 信息替换
            kingdeeReceiveNotice.setFMaterialId(mtn.get(kingdeeReceiveNotice.getFMaterialId()));

            System.out.println(kingdeeReceiveNotice);
        }

    }

    @org.junit.jupiter.api.Test
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
