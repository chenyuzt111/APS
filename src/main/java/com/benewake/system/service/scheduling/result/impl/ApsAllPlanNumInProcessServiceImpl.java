package com.benewake.system.service.scheduling.result.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsAllPlanNumInProcess;
import com.benewake.system.entity.ApsTableVersion;
import com.benewake.system.entity.enums.SchedulingResultType;
import com.benewake.system.entity.enums.TableVersionState;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.scheduling.result.ApsAllPlanNumInProcessService;
import com.benewake.system.mapper.ApsAllPlanNumInProcessMapper;
import com.benewake.system.utils.StringUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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
        implements ApsAllPlanNumInProcessService {

    @Data
    public class Item {
        String materialName;

        List<ApsAllPlanNumInProcess> apsAllPlanNumInProcesses;
    }

    @Autowired
    private ApsTableVersionService apsTableVersionService;

    @Override
    public List<Item> getProductionPlanSort() {
        List<ApsAllPlanNumInProcess> apsAllPlanNumInProcesses = getAll();
        apsAllPlanNumInProcesses = apsAllPlanNumInProcesses.stream().peek(x -> {
            String startTime = x.getStartTime();
            String s = StringUtils.formatTime(startTime);
            x.setStartTime(s);
            String endTime = x.getEndTime();
            String send = StringUtils.formatTime(endTime);
            x.setEndTime(send);
            String taskDate = x.getTaskDate();
            String s1 = StringUtils.formatDate(taskDate);
            x.setTaskDate(s1);
        }).collect(Collectors.toList());
        List<ApsAllPlanNumInProcess> sortedList = sortEntities(apsAllPlanNumInProcesses);
        List<String> nameList = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        for (ApsAllPlanNumInProcess apsProductionPlan : sortedList) {
            String fMaterialName = apsProductionPlan.getMaterialName();
            if (!nameList.contains(fMaterialName)) {
                Item item = new Item();
                ArrayList<ApsAllPlanNumInProcess> apsProductionPlans = new ArrayList<>();
                apsProductionPlans.add(apsProductionPlan);
                item.setApsAllPlanNumInProcesses(apsProductionPlans);
                item.setMaterialName(fMaterialName);
                items.add(item);
                nameList.add(fMaterialName);
            } else {
                List<Item> itemList = items.stream()
                        .filter(item -> fMaterialName.equals(item.getMaterialName()))
                        .collect(Collectors.toList());
                List<ApsAllPlanNumInProcess> apsProductionPlans = itemList.get(0).getApsAllPlanNumInProcesses();
                apsProductionPlans.add(apsProductionPlan);
            }
        }
        Collections.reverse(items);
        return items;
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
                    int timeComparison = entity2.getStartTime().compareTo(entity1.getStartTime());
                    if (timeComparison != 0) {
                        return timeComparison;
                    }

                    // 如果start_time也相等，按end_time升序排序
                    return entity1.getEndTime().compareTo(entity2.getEndTime());
                })
                .collect(Collectors.toList());
    }

    private static String convertDate(String date) {
        // 将日期格式转换为排序友好的格式，例如：11-23 -> 1123
        return date.replace("-", "");
    }


    private List<ApsAllPlanNumInProcess> getAll() {
        LambdaQueryWrapper<ApsTableVersion> apsTableVersionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsTableVersionLambdaQueryWrapper
                .eq(ApsTableVersion::getTableId, SchedulingResultType.APS_ALL_PLAN_NUM_IN_PROCESS.getCode())
                .eq(ApsTableVersion::getState , TableVersionState.SUCCESS.getCode())
                .orderByDesc(ApsTableVersion::getVersionNumber)
                .last("limit 1");
        ApsTableVersion one = apsTableVersionService.getOne(apsTableVersionLambdaQueryWrapper);
        if (one == null || one.getTableVersion() == null) {
            throw new BeneWakeException("当前还没有已经生成的甘特图数据");
        }
        LambdaQueryWrapper<ApsAllPlanNumInProcess> allPlanNumInProcessQueryWrapper = new LambdaQueryWrapper<>();
        allPlanNumInProcessQueryWrapper.eq(ApsAllPlanNumInProcess::getVersion ,one.getTableVersion());
        return baseMapper.selectList(allPlanNumInProcessQueryWrapper);
    }
}




