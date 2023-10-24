package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsAllPlanNumInProcess;
import com.benewake.system.entity.ApsProductionPlan;
import com.benewake.system.entity.vo.ReturnTest;
import com.benewake.system.service.ApsProductionPlanService;
import com.benewake.system.mapper.ApsProductionPlanMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author ASUS
* @description 针对表【aps_production_plan】的数据库操作Service实现
* @createDate 2023-10-23 16:40:42
*/
@Service
public class ApsProductionPlanServiceImpl extends ServiceImpl<ApsProductionPlanMapper, ApsProductionPlan>
    implements ApsProductionPlanService{
    @Override
    public ReturnTest getLatestCompletion() {
        List<ApsProductionPlan> apsProductionPlans = baseMapper.selectList(null);
        Set<String> materialSet = apsProductionPlans.stream().map(ApsProductionPlan::getFMaterialName).collect(Collectors.toSet());
        HashMap<String, List<ApsProductionPlan>> stringArrayListHashMap = new HashMap<>();
        for (ApsProductionPlan apsProductionPlan : apsProductionPlans) {
            String materialName = apsProductionPlan.getFMaterialName();
            if (stringArrayListHashMap.get(materialName) != null) {
                stringArrayListHashMap.get(materialName).add(apsProductionPlan);
            } else {
                ArrayList<ApsProductionPlan> apsAllPlanNumInProcesses1 = new ArrayList<>();
                apsAllPlanNumInProcesses1.add(apsProductionPlan);
                stringArrayListHashMap.put(materialName ,apsAllPlanNumInProcesses1);
            }
        }
        ReturnTest returnTest = new ReturnTest();
        returnTest.setList(materialSet);
        returnTest.setMap(stringArrayListHashMap);
        return returnTest;
    }
}




