package com.benewake.system.service.scheduling.kingdee.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsMaterialBom;
import com.benewake.system.entity.ApsMaterialProcessMapping;
import com.benewake.system.entity.ApsTableUpdateDate;
import com.benewake.system.entity.enums.BOMChangeType;
import com.benewake.system.entity.enums.InterfaceDataType;
import com.benewake.system.entity.kingdee.KingdeeMaterialBom;
import com.benewake.system.entity.kingdee.transfer.FIDToNumber;
import com.benewake.system.entity.kingdee.transfer.MaterialBomChange;
import com.benewake.system.entity.kingdee.transfer.MaterialIdToName;
import com.benewake.system.mapper.ApsMaterialProcessMappingMapper;
import com.benewake.system.mapper.ApsTableUpdateDateMapper;
import com.benewake.system.service.scheduling.kingdee.ApsMaterialBomService;
import com.benewake.system.mapper.ApsMaterialBomMapper;
import com.benewake.system.transfer.KingdeeToApsMaterialBom;
import com.kingdee.bos.webapi.entity.QueryParam;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ASUS
 * @description 针对表【aps_material_bom】的数据库操作Service实现
 * @createDate 2023-10-18 15:10:19
 */
@Service
public class ApsMaterialBomServiceImpl extends ServiceImpl<ApsMaterialBomMapper, ApsMaterialBom>
        implements ApsMaterialBomService {

    @Autowired
    private KingdeeToApsMaterialBom kingdeeToApsMaterialBom;

    @Autowired
    private K3CloudApi api;

    @Autowired
    private ApsMaterialProcessMappingMapper apsMaterialProcessMappingMapper;

    @Autowired
    private ApsTableUpdateDateMapper tableUpdateDateMapper;

    private final Map<String, String> docStatusMap = new HashMap<>();

    private final Map<String, String> pickStatusMap = new HashMap<>();

    private Integer maxVersionIncr = -1;

    @PostConstruct
    public void init() {
        docStatusMap.put("Z", "暂存");
        docStatusMap.put("A", "创建");
        docStatusMap.put("B", "审核中");
        docStatusMap.put("C", "已审核");
        docStatusMap.put("D", "重新审核");

        pickStatusMap.put("1", "标准件");
        pickStatusMap.put("2", "返还件");
        pickStatusMap.put("3", "替代件");
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateAllDataVersions() throws Exception {
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("ENG_BOM");
        queryParam.setFieldKeys("FNumber,FUseOrgId,FMaterialId,FITEMNAME,FDocumentStatus,FMaterialIDChild,FCHILDITEMNAME,FNumerator,FDenominator,FFixScrapQtyLot,FMaterialType,FReplaceType,FReplaceGroup,FScrapRate,FForBidStatus,FExpireDate");

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
        // 物料映射表
        Map<String, String> fMaterialIdToFNumber = getFMaterialIdToFNumber();

        // 创建一个映射，用于存储每个FMATERIALID对应的最大FNumber
        List<String> maxFNumbersList = getMaxFNumbersList(result, fMaterialIdToFNumber);

        // 再次遍历result，根据最大FNumber打印出相应的记录
        List<ApsMaterialProcessMapping> apsMaterialProcessMappings = apsMaterialProcessMappingMapper.selectList(null);
        Map<String, String> materialToProcess = apsMaterialProcessMappings.stream().collect(Collectors.toMap(ApsMaterialProcessMapping::getFMaterialId, ApsMaterialProcessMapping::getProcess, (existing, replacement) -> existing));
        ArrayList<ApsMaterialBom> apsMaterialBoms = getApsMaterialBoms(result, fMaterialIdToFNumber, maxFNumbersList, materialToProcess);
        if (CollectionUtils.isEmpty(apsMaterialBoms)) {
            ApsMaterialBom apsMaterialBom = new ApsMaterialBom();
            apsMaterialBom.setVersion(maxVersionIncr);
            return save(apsMaterialBom);
        }
        ApsTableUpdateDate tableUpdateDate = new ApsTableUpdateDate();
        tableUpdateDate.setTableId(InterfaceDataType.MATERIAL_BOM.getCode());
        tableUpdateDate.setUpdateDate(new Date());
        tableUpdateDateMapper.insert(tableUpdateDate);
        return saveBatch(apsMaterialBoms);
    }

    private List<String> getMaxFNumbersList(List<KingdeeMaterialBom> result, Map<String, String> fMaterialIdToFNumber) {
        Map<String, String> maxFNumberMap = new HashMap<>();
        // 创建一个列表，用于存储最大FNumber
        List<String> maxFNumbersList = new ArrayList<>();
        // 遍历result，找到每个不同的FMATERIALID对应的最大FNumber
        for (KingdeeMaterialBom kingdeeMaterialBom : result) {
            String materialId = fMaterialIdToFNumber.get(kingdeeMaterialBom.getFMaterialID());
            String fNumber = kingdeeMaterialBom.getFNumber();
            String fMaterialType = kingdeeMaterialBom.getFMaterialType();
            kingdeeMaterialBom.setFMaterialType(pickStatusMap.getOrDefault(fMaterialType, fMaterialType));
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
        return maxFNumbersList;
    }

    private ArrayList<ApsMaterialBom> getApsMaterialBoms(List<KingdeeMaterialBom> result, Map<String, String> fMaterialIdToFNumber, List<String> maxFNumbersList, Map<String, String> materialToProcess) {
        long l = System.currentTimeMillis();
        ArrayList<ApsMaterialBom> apsMaterialBoms = new ArrayList<>();
        for (KingdeeMaterialBom kingdeeMaterialBom : result) {
            String fNumber = kingdeeMaterialBom.getFNumber();
            if (maxFNumbersList.contains(fNumber)) {
                // 获取 FDocumentStatus 的 id
                String docStatusId = kingdeeMaterialBom.getFDocumentStatus();
                // 使用映射 HashMap 获取状态文字
                String docStatusText = docStatusMap.get(docStatusId);
                if (docStatusText != null) {
                    kingdeeMaterialBom.setFDocumentStatus(docStatusText);
                }
                kingdeeMaterialBom.setFMaterialID(fMaterialIdToFNumber.get(kingdeeMaterialBom.getFMaterialID()));
                kingdeeMaterialBom.setFMaterialIDChild(fMaterialIdToFNumber.get(kingdeeMaterialBom.getFMaterialIDChild()));
                // 打印整条记录
                ApsMaterialBom apsMaterialBom = kingdeeToApsMaterialBom.convert(kingdeeMaterialBom, maxVersionIncr);
                String process = materialToProcess.get(apsMaterialBom.getFMaterialIdChild());
                apsMaterialBom.setProcess(process);
                apsMaterialBoms.add(apsMaterialBom);
            }
        }
        long l1 = System.currentTimeMillis();
        System.err.println("转化：" + (l1 - l));
        return apsMaterialBoms;
    }

    @Override
    public List<Object> selectVersionPageList(Integer pass, Integer size, List versionToChVersionArrayList) {
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateDataVersions() throws Exception {
        maxVersionIncr = getMaxVersionIncr();
        LambdaQueryWrapper<ApsTableUpdateDate> tableUpdateDateQueryWrapper = new LambdaQueryWrapper<ApsTableUpdateDate>()
                .eq(ApsTableUpdateDate::getTableId, InterfaceDataType.MATERIAL_BOM.getCode());
        ApsTableUpdateDate tableUpdateDate = tableUpdateDateMapper.selectOne(tableUpdateDateQueryWrapper);
        if (tableUpdateDate == null) {
            return updateAllDataVersions();
        } else {
            return updateProtionDate(tableUpdateDate);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    Boolean updateProtionDate(ApsTableUpdateDate tableUpdateDate) throws Exception {
        List<MaterialBomChange> materialBomChangeList = getMaterialBomChanges(tableUpdateDate);
        Map<String, String> fMaterialIdToFNumber = getFMaterialIdToFNumber();
        List<MaterialBomChange> deleteList = materialBomChangeList.stream()
                .filter(x -> x.getFChangeLabel().equals(BOMChangeType.CHANGE_BEFORE.getCode()) ||
                        x.getFChangeLabel().equals(BOMChangeType.DELETE.getCode()))
                .peek(materialBomChange -> {
                    materialBomChange.setFParentMaterialId(fMaterialIdToFNumber.get(materialBomChange.getFParentMaterialId()));
                    materialBomChange.setFMATERIALIDCHILD(fMaterialIdToFNumber.get(materialBomChange.getFMATERIALIDCHILD()));
                })
                .collect(Collectors.toList());
        //查了除了删除的所有数据
        List<MaterialBomChange> addList = materialBomChangeList.stream()
                .filter(x -> x.getFChangeLabel().equals(BOMChangeType.CHANGE_AFTER.getCode()) ||
                        x.getFChangeLabel().equals(BOMChangeType.ADD.getCode()))
                .collect(Collectors.toList());
        baseMapper.insertListNotDelete(deleteList);

        List<KingdeeMaterialBom> addByFMaterialIdAndChild = getAddByFMaterialIdAndChild(addList);
        List<ApsMaterialBom> apsMaterialBoms = null;
        if (CollectionUtils.isNotEmpty(addByFMaterialIdAndChild)) {
            List<String> maxFNumbersList = getMaxFNumbersList(addByFMaterialIdAndChild, fMaterialIdToFNumber);
            List<ApsMaterialProcessMapping> apsMaterialProcessMappings = apsMaterialProcessMappingMapper.selectList(null);
            Map<String, String> materialToProcess = apsMaterialProcessMappings.stream().collect(Collectors.toMap(ApsMaterialProcessMapping::getFMaterialId, ApsMaterialProcessMapping::getProcess, (existing, replacement) -> existing));
            // 创建一个映射，用于存储每个FMATERIALID对应的最大FNumber
            apsMaterialBoms = getApsMaterialBoms(addByFMaterialIdAndChild, fMaterialIdToFNumber, maxFNumbersList, materialToProcess);
        }
        tableUpdateDate.setUpdateDate(new Date());
        tableUpdateDateMapper.updateById(tableUpdateDate);
        saveBatch(apsMaterialBoms);
        return true;
    }

    public List<KingdeeMaterialBom> getAddByFMaterialIdAndChild(List<MaterialBomChange> addList) throws Exception {
        if (CollectionUtils.isEmpty(addList)) {
            return null;
        }
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("ENG_BOM");
        queryParam.setFieldKeys("FNumber,FUseOrgId,FMATERIALID,FDocumentStatus,FMaterialIDChild,FNUMERATOR,FDENOMINATOR,FFIXSCRAPQTYLOT,FSCRAPRATE,FForbidStatus,FEXPIREDATE,FReplaceGroup");
        List<String> queryFilters = new ArrayList<>();
        queryFilters.add("FUseOrgId = '1'");
        queryFilters.add("FForbidStatus = 'A'");
        List<String> addFilters = addList.stream()
                .map(x -> "(FMATERIALID =" + x.getFParentMaterialId() + " and FMaterialIDChild =" + x.getFMATERIALIDCHILD() + ")")
                .collect(Collectors.toList());
        // 使用 Java 中的日期类获取当前日期，并将其格式化为字符串
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateStr = dateFormat.format(currentDate);
        queryFilters.add("TO_DATE(FEXPIREDATE, 'yyyy-MM-dd') >= TO_DATE('" + currentDateStr + "', 'yyyy-MM-dd')");
        String join = String.join(" and ", queryFilters);
        if (CollectionUtils.isNotEmpty(addFilters)) {
            String add = String.join(" or ", addFilters);
            join = join + "and (" + add + ")";
        }
        queryParam.setFilterString(join);
        List<KingdeeMaterialBom> result = api.executeBillQuery(queryParam, KingdeeMaterialBom.class);
        return result;
    }

    private List<MaterialBomChange> getMaterialBomChanges(ApsTableUpdateDate tableUpdateDate) throws Exception {
        List<String> queryFilters = new ArrayList<>();
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("ENG_ECNOrder");
        queryParam.setFieldKeys("FApproveDate,FChangeLabel,FBOMVERSION,FParentMaterialId,FMaterialIDChild,FCHILDITEMNAME");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date updateDate = tableUpdateDate.getUpdateDate();
        queryFilters.add("FApproveDate > '" + sdf.format(updateDate) + "'");
        queryFilters.add("FDocumentStatus = 'C'");     // 单据状态=已审核
        queryFilters.add("FCancelStatus = 'A'");       // 作废状态=未作废
        queryParam.setFilterString(String.join(" and ", queryFilters));
        List<MaterialBomChange> result = api.executeBillQuery(queryParam, MaterialBomChange.class);
        return result;
    }

    private Map<String, String> getFMaterialIdToFNumber() throws Exception {
        QueryParam queryParam;
        queryParam = new QueryParam();
        queryParam.setFormId("BD_MATERIAL");
        queryParam.setFieldKeys("FMaterialId,FNumber");
        List<MaterialIdToName> midToName = api.executeBillQuery(queryParam, MaterialIdToName.class);
        Map<String, String> fMaterialIdToFNumber = new HashMap<>();
        midToName.forEach(c -> {
            fMaterialIdToFNumber.put(c.getFMaterialId(), c.getFNumber());
        });
        return fMaterialIdToFNumber;
    }

    private Map<String, String> getFIDToFNumber() throws Exception {
        QueryParam queryParam;
        //BOM版本号映射表
        queryParam = new QueryParam();
        queryParam.setFormId("ENG_BOM");//设置查询参数的表单ID为BD_MATERIAL
        queryParam.setFieldKeys("FID,FNumber");
        List<FIDToNumber> bidToNameList = api.executeBillQuery(queryParam, FIDToNumber.class);//调用api的executeBillQuery方法进行查询queryParam，并且传入查询参数和目标数据类型MaterialIdToName.class，返回的数据将被转换为MaterialIdToName类型的对象列表，其实是一种映射关系
        Map<String, String> FIDToNumberMap = new HashMap<>();
        bidToNameList.forEach(fidToNumber -> {//遍历midtoname列表并且对列表中的数据做括号内的操作
            FIDToNumberMap.put(fidToNumber.getFID(), fidToNumber.getFNumber());//将物料的id作为键，物料编号作为值，将键值对添加到映射表中
        });
        return FIDToNumberMap;
    }
}




