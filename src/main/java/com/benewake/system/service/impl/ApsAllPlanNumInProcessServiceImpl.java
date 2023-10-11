package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsAllPlanNumInProcess;
import com.benewake.system.entity.ApsTableVersion;
import com.benewake.system.entity.enums.TableVersionState;
import com.benewake.system.entity.vo.ReturnTest;
import com.benewake.system.mapper.ApsTableVersionMapper;
import com.benewake.system.service.ApsAllPlanNumInProcessService;
import com.benewake.system.mapper.ApsAllPlanNumInProcessMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ASUS
 * @description 针对表【aps_all_plan_num_in_process】的数据库操作Service实现
 * @createDate 2023-10-10 19:35:45
 */
@Service
public class ApsAllPlanNumInProcessServiceImpl extends ServiceImpl<ApsAllPlanNumInProcessMapper, ApsAllPlanNumInProcess>
        implements ApsAllPlanNumInProcessService {

    @Autowired
    private ApsTableVersionMapper apsTableVersionMapper;

    @Override
    public ReturnTest getLatestCompletion() {
        LambdaQueryWrapper<ApsAllPlanNumInProcess> apsAllPlanNumInProcessLambdaQueryWrapper = new LambdaQueryWrapper<>();

        List<ApsAllPlanNumInProcess> apsAllPlanNumInProcesses = baseMapper.selectList(apsAllPlanNumInProcessLambdaQueryWrapper);
        Set<String> materialSet = apsAllPlanNumInProcesses.stream().map(ApsAllPlanNumInProcess::getMaterialName).collect(Collectors.toSet());
        HashMap<String, ArrayList<ApsAllPlanNumInProcess>> stringArrayListHashMap = new HashMap<String, ArrayList<ApsAllPlanNumInProcess>>();
        for (ApsAllPlanNumInProcess apsAllPlanNumInProcess : apsAllPlanNumInProcesses) {
            String materialName = apsAllPlanNumInProcess.getMaterialName();
            if (stringArrayListHashMap.get(materialName) != null) {
                stringArrayListHashMap.get(materialName).add(apsAllPlanNumInProcess);
            } else {
                ArrayList<ApsAllPlanNumInProcess> apsAllPlanNumInProcesses1 = new ArrayList<>();
                apsAllPlanNumInProcesses1.add(apsAllPlanNumInProcess);
                stringArrayListHashMap.put(materialName ,apsAllPlanNumInProcesses1);
            }
        }
        ReturnTest returnTest = new ReturnTest();
        returnTest.setList(materialSet);
        returnTest.setMap(stringArrayListHashMap);
        return returnTest;
    }

    private Integer getVersionNumber() {
        LambdaQueryWrapper<ApsTableVersion> apsTableVersionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsTableVersionLambdaQueryWrapper.eq(ApsTableVersion::getState, TableVersionState.SCHEDULING.getCode())
                .orderByDesc(ApsTableVersion::getVersionNumber)
                .last("limit 1");
        ApsTableVersion apsTableVersion = apsTableVersionMapper.selectOne(apsTableVersionLambdaQueryWrapper);
        return apsTableVersion != null ? apsTableVersion.getVersionNumber() : 0;
    }
}




