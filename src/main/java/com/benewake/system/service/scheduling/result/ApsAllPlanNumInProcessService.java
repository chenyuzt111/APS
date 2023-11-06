package com.benewake.system.service.scheduling.result;

import com.benewake.system.entity.ApsAllPlanNumInProcess;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.service.scheduling.result.impl.ApsAllPlanNumInProcessServiceImpl;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_all_plan_num_in_process】的数据库操作Service
* @createDate 2023-11-01 09:11:27
*/
public interface ApsAllPlanNumInProcessService extends IService<ApsAllPlanNumInProcess> ,ApsSchedulingResuleBase{

    List<ApsAllPlanNumInProcessServiceImpl.Item> getProductionPlanSort();
}
