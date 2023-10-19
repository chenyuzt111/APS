package com.benewake.system.service.kingdee.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsMaterialBom;
import com.benewake.system.entity.kingdee.KingdeeMaterialBom;
import com.benewake.system.entity.kingdee.transfer.MaterialIdToName;
import com.benewake.system.service.kingdee.ApsMaterialBomService;
import com.benewake.system.mapper.ApsMaterialBomMapper;
import com.benewake.system.transfer.KingdeeToApsMaterialBom;
import com.kingdee.bos.webapi.entity.QueryParam;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

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

    @Override
    public Boolean updateDataVersions() throws Exception {
        Integer maxVersionIncr = getMaxVersionIncr();
        QueryParam queryParam = new QueryParam();
        queryParam.setFormId("ENG_BOM");
        queryParam.setFieldKeys("FNumber,FUseOrgId,FMaterialId,FDocumentStatus,FMaterialIDChild,FNumerator,FDenominator,FFixScrapQtyLot,FMaterialType,FReplaceType,FScrapRate,FForBidStatus,FExpireDate");

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
        Map<String,String> mtn = new HashMap<>();
        midToName.forEach(c->{
            mtn.put(c.getFMaterialId(),c.getFNumber());
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
        ArrayList<ApsMaterialBom> apsMaterialBoms = new ArrayList<>();
        for (KingdeeMaterialBom kingdeeMaterialBom : result) {
            String fNumber = kingdeeMaterialBom.getFNumber();
            // 获取 FDocumentStatus 的 id
            String docStatusId = kingdeeMaterialBom.getFDocumentStatus();
            // 使用映射 HashMap 获取状态文字
            String docStatusText = docStatusMap.get(docStatusId);
            if (docStatusText != null) {
                kingdeeMaterialBom.setFDocumentStatus(docStatusText);
            }
            kingdeeMaterialBom.setFMaterialID(mtn.get(kingdeeMaterialBom.getFMaterialID()));
            kingdeeMaterialBom.setFMaterialIDChild(mtn.get(kingdeeMaterialBom.getFMaterialIDChild()));
            if (maxFNumbersList.contains(fNumber) ) {
                // 打印整条记录
                ApsMaterialBom apsMaterialBom = kingdeeToApsMaterialBom.convert(kingdeeMaterialBom, maxVersionIncr);
                apsMaterialBoms.add(apsMaterialBom);
            }
        }
        return saveBatch(apsMaterialBoms);
    }

    @Override
    public List<Object> selectVersionPageList(Integer pass, Integer size, List versionToChVersionArrayList) {
        return null;
    }
}




