package com.benewake.system.service.scheduling.result.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsProductionPlan;
import com.benewake.system.entity.dto.ApsProductionPlanDto;
import com.benewake.system.entity.enums.SchedulingResultType;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.mapper.ApsProductionPlanMapper;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.scheduling.result.ApsProductionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author ASUS
 * @description 针对表【aps_production_plan】的数据库操作Service实现
 * @createDate 2023-10-23 16:40:42
 */
@Service
public class ApsProductionPlanServiceImpl extends ServiceImpl<ApsProductionPlanMapper, ApsProductionPlan>
        implements ApsProductionPlanService {
    @Autowired
    private ApsTableVersionService apsTableVersionService;

    @Autowired
    private ApsProductionPlanMapper apsProductionPlanMapper;

    @Override
    public Map<String, List<ApsProductionPlan>> getProductionPlan() {
//        Integer apsTableVersion = getApsTableVersion(SchedulingResultType.APS_PRODUCTION_PLAN.getCode() ,apsTableVersionService);
//        LambdaQueryWrapper<ApsProductionPlan> apsProductionPlanLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        apsProductionPlanLambdaQueryWrapper.eq(ApsProductionPlan::getVersion, apsTableVersion);
//        List<ApsProductionPlan> apsProductionPlans = baseMapper.selectList(apsProductionPlanLambdaQueryWrapper);
//        // 使用流和Collectors.groupingBy来根据fMaterialName分组
//        Map<String, List<ApsProductionPlan>> groupedPlans = apsProductionPlans.stream()
//                .collect(Collectors.groupingBy(ApsProductionPlan::getFMaterialName));
//        // 对每个分组进行排序
//        groupedPlans.values().forEach(plans -> plans.sort(Comparator
//                .comparing(ApsProductionPlan::getFActualStartTime)
//                .reversed() // 按fActualStartTime从大到小排
//                .thenComparing(ApsProductionPlan::getFActualCompletionTime)));
//        // 现在，groupedPlans 中的分组已经按照你的要求排序
////        Set<String> materialSet = apsProductionPlans.stream().map(ApsProductionPlan::getFMaterialName).collect(Collectors.toSet());
////        HashMap<String, List<ApsProductionPlan>> stringArrayListHashMap = new HashMap<>();
////        for (ApsProductionPlan apsProductionPlan : apsProductionPlans) {
////            String materialName = apsProductionPlan.getFMaterialName();
////            if (stringArrayListHashMap.get(materialName) != null) {
////                stringArrayListHashMap.get(materialName).add(apsProductionPlan);
////            } else {
////                ArrayList<ApsProductionPlan> apsAllPlanNumInProcesses1 = new ArrayList<>();
////                apsAllPlanNumInProcesses1.add(apsProductionPlan);
////                stringArrayListHashMap.put(materialName, apsAllPlanNumInProcesses1);
////            }
////        }
////        ReturnTest returnTest = new ReturnTest();
////        returnTest.setList(materialSet);
////        returnTest.setMap(stringArrayListHashMap);
        return null;
    }

    @Override
    public PageResultVo<ApsProductionPlanDto> getAllPage(Integer page, Integer size) {
        Integer apsTableVersion = getApsTableVersion(SchedulingResultType.APS_PRODUCTION_PLAN.getCode() ,apsTableVersionService);
        Page<ApsProductionPlan> apsProductionPlanPage = new Page<>();
        apsProductionPlanPage.setSize(size);
        apsProductionPlanPage.setCurrent(page);
        Page<ApsProductionPlanDto> planPage = apsProductionPlanMapper.selectPageList(apsProductionPlanPage ,apsTableVersion);
        PageResultVo<ApsProductionPlanDto> apsProductionPlanPageResultVo = new PageResultVo<>();
        apsProductionPlanPageResultVo.setList(planPage.getRecords());
        apsProductionPlanPageResultVo.setPage(page);
        apsProductionPlanPageResultVo.setTotal(planPage.getTotal());
        apsProductionPlanPageResultVo.setPages(planPage.getPages());
        apsProductionPlanPageResultVo.setSize(size);
        return apsProductionPlanPageResultVo;
    }
}




