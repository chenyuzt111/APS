package com.benewake.system.service;

import com.benewake.system.entity.ApsAllPlanNumInProcess;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.vo.ReturnTest;

/**
* @author ASUS
* @description 针对表【aps_all_plan_num_in_process】的数据库操作Service
* @createDate 2023-10-10 19:35:45
*/
//TODO 测试
public interface ApsAllPlanNumInProcessService extends IService<ApsAllPlanNumInProcess> {

    ReturnTest getLatestCompletion();
}
