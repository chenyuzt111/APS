package com.benewake.system.service.scheduling.kingdee.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsMaterialBom;
import com.benewake.system.entity.ApsMaterialNameMapping;
import com.benewake.system.entity.ApsMaterialProcessMapping;
import com.benewake.system.entity.ApsTableUpdateDate;
import com.benewake.system.entity.dto.ApsMaterialBomDto;
import com.benewake.system.entity.enums.BOMChangeType;
import com.benewake.system.entity.enums.InterfaceDataType;
import com.benewake.system.entity.kingdee.KingdeeMaterialBom;
import com.benewake.system.entity.kingdee.transfer.FMaterialIdToSub;
import com.benewake.system.entity.kingdee.transfer.MaterialBomChange;
import com.benewake.system.entity.kingdee.transfer.MaterialIdToName;
import com.benewake.system.mapper.ApsMaterialProcessMappingMapper;
import com.benewake.system.mapper.ApsTableUpdateDateMapper;
import com.benewake.system.service.ApsMaterialProcessMappingService;
import com.benewake.system.service.scheduling.kingdee.ApsMaterialBomService;
import com.benewake.system.mapper.ApsMaterialBomMapper;
import com.benewake.system.service.scheduling.kingdee.ApsMaterialNameMappingService;
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
    private ApsMaterialBomMapper apsMaterialBomMapper;

    @Autowired
    private ApsMaterialProcessMappingMapper apsMaterialProcessMappingMapper;

    @Autowired
    private ApsTableUpdateDateMapper tableUpdateDateMapper;

    @Autowired
    private ApsMaterialNameMappingService apsMaterialNameMappingService;

    @Autowired
    private ApsMaterialProcessMappingService materialProcessMappingService;

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
        queryParam.setFieldKeys("FNumber,FUseOrgId,FMaterialId,FITEMNAME,FITEMMODEL,FDocumentStatus,FMaterialIDChild,FCHILDITEMNAME,FCHILDITEMMODEL,FNumerator,FDenominator,FFixScrapQtyLot,FMaterialType,FReplaceType,FReplaceGroup,FScrapRate,FForBidStatus,FExpireDate");

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
        Map<String, String> materialToProcess = apsMaterialProcessMappings.stream()
                .collect(Collectors.toMap(ApsMaterialProcessMapping::getFMaterialId, ApsMaterialProcessMapping::getProcess, (existing, replacement) -> existing));
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
        List<ApsMaterialNameMapping> nameMappings = getApsMaterialNameMappings(result);
        //新增之前删除
        apsMaterialNameMappingService.remove(null);
        apsMaterialNameMappingService.saveBatch(nameMappings);
        remove(null);
        return saveBatch(apsMaterialBoms);
    }

    private List<ApsMaterialNameMapping> getApsMaterialNameMappings(List<KingdeeMaterialBom> result) {
        List<ApsMaterialNameMapping> nameMappings = result.stream().map(x -> {
            ApsMaterialNameMapping nameMapping = new ApsMaterialNameMapping();
            nameMapping.setFMaterialId(x.getFMaterialID());
            nameMapping.setFMaterialName(x.getFitemName());
            nameMapping.setFItemModel(x.getFItemModel());
            return nameMapping;
        }).collect(Collectors.toList());
        nameMappings.addAll(result.stream().map(x -> {
            ApsMaterialNameMapping nameMapping = new ApsMaterialNameMapping();
            nameMapping.setFMaterialId(x.getFMaterialIDChild());
            nameMapping.setFMaterialName(x.getFCHILDITEMNAME());
            nameMapping.setFItemModel(x.getFChildItemModel());
            return nameMapping;
        }).collect(Collectors.toList()));
        nameMappings = nameMappings.stream().distinct().collect(Collectors.toList());
        return nameMappings;
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

    private ArrayList<ApsMaterialBom> getApsMaterialBoms(List<KingdeeMaterialBom> result, Map<String, String> fMaterialIdToFNumber,
                                                         List<String> maxFNumbersList, Map<String, String> materialToProcess) {
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
        return apsMaterialBoms;
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

    @Override
    public void insertVersionIncr() {
        apsMaterialBomMapper.insertSelectVersionIncr();
    }

    @Override
    public Page selectPageList(Page page, List tableVersionList) {
        Page<ApsMaterialBomDto> materialBomDtoPage = apsMaterialBomMapper.selectPageList(page, tableVersionList);
        return materialBomDtoPage;
    }

    @Transactional(rollbackFor = Exception.class)
    Boolean updateProtionDate(ApsTableUpdateDate tableUpdateDate) throws Exception {
        //查询变更表的数据
        List<MaterialBomChange> materialBomChangeList = getMaterialBomChanges(tableUpdateDate);
        Map<String, String> fMaterialIdToFNumber = getFMaterialIdToFNumber();
        //需要删除的
        List<MaterialBomChange> deleteList = materialBomChangeList.stream()
                .filter(x -> x.getFChangeLabel().equals(BOMChangeType.CHANGE_BEFORE.getCode()) ||
                        x.getFChangeLabel().equals(BOMChangeType.DELETE.getCode()))
                .peek(materialBomChange -> {
                    materialBomChange.setFParentMaterialId(fMaterialIdToFNumber.get(materialBomChange.getFParentMaterialId()));
                    materialBomChange.setFMATERIALIDCHILD(fMaterialIdToFNumber.get(materialBomChange.getFMATERIALIDCHILD()));
                })
                .collect(Collectors.toList());
        //重新插入 insert into select除了删除的所有数据
        List<MaterialBomChange> addList = materialBomChangeList.stream()
                .filter(x -> x.getFChangeLabel().equals(BOMChangeType.CHANGE_AFTER.getCode()) ||
                        x.getFChangeLabel().equals(BOMChangeType.ADD.getCode()))
                .collect(Collectors.toList());
        baseMapper.insertListNotDelete(deleteList);

        List<KingdeeMaterialBom> addByFMaterialIdAndChild = getAddByFMaterialIdAndChild(addList);
        List<ApsMaterialBom> apsMaterialBoms = null;
        if (CollectionUtils.isNotEmpty(addByFMaterialIdAndChild)) {
            List<ApsMaterialNameMapping> apsMaterialNameMappings = getApsMaterialNameMappings(addByFMaterialIdAndChild);
            List<String> materidlIds = apsMaterialNameMappings.stream().map(ApsMaterialNameMapping::getFMaterialId).collect(Collectors.toList());
            //将新的物料名称对应关系 添加进去
            apsMaterialNameMappingService.remove(new LambdaQueryWrapper<ApsMaterialNameMapping>().in(ApsMaterialNameMapping::getFMaterialId, materidlIds));
            apsMaterialNameMappingService.saveBatch(apsMaterialNameMappings);
            List<String> maxFNumbersList = getMaxFNumbersList(addByFMaterialIdAndChild, fMaterialIdToFNumber);
            List<ApsMaterialProcessMapping> apsMaterialProcessMappings = apsMaterialProcessMappingMapper.selectList(null);
            Map<String, String> materialToProcess = apsMaterialProcessMappings.stream().collect(Collectors.toMap(ApsMaterialProcessMapping::getFMaterialId, ApsMaterialProcessMapping::getProcess, (existing, replacement) -> existing));
            // 创建一个映射，用于存储每个FMATERIALID对应的最大FNumber
            apsMaterialBoms = getApsMaterialBoms(addByFMaterialIdAndChild, fMaterialIdToFNumber, maxFNumbersList, materialToProcess);
        }
        tableUpdateDate.setUpdateDate(new Date());
        tableUpdateDateMapper.updateById(tableUpdateDate);

        if (CollectionUtils.isNotEmpty(apsMaterialBoms)) {
            Set<String> fMaterialIdSet = apsMaterialBoms.stream()
                    .map(x -> "'" + x.getFMaterialIdChild() + "'").collect(Collectors.toSet());
            updateMaterialIdProcessMapping(fMaterialIdSet);
            saveBatch(apsMaterialBoms);
        }
        return true;
    }

    public void updateMaterialIdProcessMapping(Set<String> fMaterialIdSet) throws Exception {
        // First Query
        QueryParam materialQueryParam = new QueryParam();
        materialQueryParam.setFormId("BD_MATERIAL");
        materialQueryParam.setFieldKeys("FMaterialId,FNumber");
        materialQueryParam.setFilterString("FNumber in (" + String.join(",", fMaterialIdSet) + ")");
        List<MaterialIdToName> materialIdToNames = api.executeBillQuery(materialQueryParam, MaterialIdToName.class);

        // Second Query
        QueryParam substitutionQueryParam = new QueryParam();
        substitutionQueryParam.setFormId("ENG_Substitution");
        substitutionQueryParam.setFieldKeys("FNumber,FMaterialID,FSubMaterialID,FEffectDate,FExpireDate");

        List<String> materialIds = materialIdToNames.stream()
                .map(x -> "'" + x.getFMaterialId() + "'")
                .collect(Collectors.toList());

        substitutionQueryParam.setFilterString("FMaterialID in (" + String.join(",", materialIds) + ")");

        List<FMaterialIdToSub> substitutionResult = api.executeBillQuery(substitutionQueryParam, FMaterialIdToSub.class);
        if (CollectionUtils.isEmpty(substitutionResult)) {
            return;
        }

        // Third Query
        List<String> subMaterialIds = substitutionResult.stream()
                .map(x -> "'" + x.getFSubMaterialID() + "'")
                .collect(Collectors.toList());

        QueryParam subMaterialQueryParam = new QueryParam();
        subMaterialQueryParam.setFormId("BD_MATERIAL");
        subMaterialQueryParam.setFieldKeys("FMaterialId,FNumber");
        subMaterialQueryParam.setFilterString("FMaterialId in (" + String.join(",", subMaterialIds) + ")");
        List<MaterialIdToName> subMaterialIdToNames = api.executeBillQuery(subMaterialQueryParam, MaterialIdToName.class);

        // Create maps
        Map<String, String> materialIdToNumberMap = materialIdToNames.stream()
                .collect(Collectors.toMap(MaterialIdToName::getFMaterialId, MaterialIdToName::getFNumber));

        Map<String, String> subMaterialIdToNumberMap = subMaterialIdToNames.stream()
                .collect(Collectors.toMap(MaterialIdToName::getFMaterialId, MaterialIdToName::getFNumber));

        Set<String> fMaterialId = substitutionResult.stream().map(x ->
                materialIdToNumberMap.get(x.getFMaterialID())).collect(Collectors.toSet());
        Set<String> fSubMaterialId = substitutionResult.stream().map(x ->
                subMaterialIdToNumberMap.get(x.getFSubMaterialID())).collect(Collectors.toSet());
        fMaterialId.addAll(fSubMaterialId);
        LambdaQueryWrapper<ApsMaterialProcessMapping> processMappingLambdaQueryWrapper = new LambdaQueryWrapper<>();
        processMappingLambdaQueryWrapper.in(ApsMaterialProcessMapping::getFMaterialId, fMaterialId);
        List<ApsMaterialProcessMapping> apsMaterialProcessMappings = materialProcessMappingService.getBaseMapper().selectList(processMappingLambdaQueryWrapper);
        Map<String, String> fMaterialIdToProcessMap = apsMaterialProcessMappings.stream().collect(Collectors.toMap(ApsMaterialProcessMapping::getFMaterialId, ApsMaterialProcessMapping::getProcess));
        // Update test objects

        List<ApsMaterialProcessMapping> processMappings = substitutionResult.stream().map(testObj -> {
            if (fMaterialIdToProcessMap.get(materialIdToNumberMap.get(testObj.getFMaterialID())) != null &&
                    fMaterialIdToProcessMap.get(subMaterialIdToNumberMap.get(testObj.getFSubMaterialID())) == null) {
                ApsMaterialProcessMapping apsMaterialProcessMapping = new ApsMaterialProcessMapping();
                apsMaterialProcessMapping.setFMaterialId(subMaterialIdToNumberMap.get(testObj.getFSubMaterialID()));
                apsMaterialProcessMapping.setProcess(fMaterialIdToProcessMap.get(materialIdToNumberMap.get(testObj.getFMaterialID())));
                return apsMaterialProcessMapping;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        materialProcessMappingService.saveBatch(processMappings);
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
        midToName.forEach(materialIdToName ->
                fMaterialIdToFNumber.put(materialIdToName.getFMaterialId(), materialIdToName.getFNumber()));
        return fMaterialIdToFNumber;
    }

}




