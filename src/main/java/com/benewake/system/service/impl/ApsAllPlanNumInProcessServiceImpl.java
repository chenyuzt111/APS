package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsAllPlanNumInProcess;
import com.benewake.system.entity.ApsProductionPlan;
import com.benewake.system.service.ApsAllPlanNumInProcessService;
import com.benewake.system.mapper.ApsAllPlanNumInProcessMapper;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author ASUS
* @description 针对表【aps_all_plan_num_in_process】的数据库操作Service实现
* @createDate 2023-11-01 09:11:27
*/
@Service
public class ApsAllPlanNumInProcessServiceImpl extends ServiceImpl<ApsAllPlanNumInProcessMapper, ApsAllPlanNumInProcess>
    implements ApsAllPlanNumInProcessService{

    @Data
    public class Item {
        String materialName;

        List<ApsAllPlanNumInProcess> apsAllPlanNumInProcesses;
    }

    @Override
    public List<Item> getProductionPlanSort() {
//        List<ApsProductionPlan> all = getAll();
////        List<String> materialNameList = all.stream().map(ApsProductionPlan::getFMaterialName).collect(Collectors.toList());
//        List<String> nameList = new ArrayList<>();
//        List<ApsProductionPlan> sortedList = all.stream()
//                .filter(plan -> !"未开始".equals(plan.getFActualStartTime())) // 过滤未完成的任务
//                .sorted(Comparator.comparing(ApsProductionPlan::getFActualStartTime, Comparator.reverseOrder())
//                        .thenComparing(ApsProductionPlan::getFActualCompletionTime))
//                .collect(Collectors.toList());
//
//        List<Item> items = new ArrayList<>();
//        for (ApsProductionPlan apsProductionPlan : sortedList) {
//            String fMaterialName = apsProductionPlan.getFMaterialName();
//            if (!nameList.contains(fMaterialName)) {
//                Item item = new Item();
//                ArrayList<ApsProductionPlan> apsProductionPlans = new ArrayList<>();
//                apsProductionPlans.add(apsProductionPlan);
//                item.setApsAllPlanNumInProcesses(apsProductionPlans);
//                item.setMaterialName(fMaterialName);
//                items.add(item);
//                nameList.add(fMaterialName);
//            } else {
//                List<Item> itemList = items.stream()
//                        .filter(item -> fMaterialName.equals(item.getMaterialName()))
//                        .collect(Collectors.toList());
//                List<ApsProductionPlan> apsProductionPlans = itemList.get(0).getApsProductionPlans();
//                apsProductionPlans.add(apsProductionPlan);
//            }
//        }

        return null;
    }
}




