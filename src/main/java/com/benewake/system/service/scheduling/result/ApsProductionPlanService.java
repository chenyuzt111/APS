package com.benewake.system.service.scheduling.result;

import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.ApsProductionPlan;
import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ResultColPageVo;

/**
* @author ASUS
* @description 针对表【aps_production_plan】的数据库操作Service
* @createDate 2023-10-23 16:40:42
*/
public interface ApsProductionPlanService extends IService<ApsProductionPlan> ,ApsSchedulingResuleBase{

}
