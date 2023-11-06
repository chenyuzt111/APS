package com.benewake.system.service.scheduling.result;

import com.benewake.system.entity.ApsSemiFinishedGoodsProductionPlan;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.vo.PageListRestVo;

/**
* @author ASUS
* @description 针对表【aps_semi_finished_goods_production_plan】的数据库操作Service
* @createDate 2023-11-02 11:38:43
*/
public interface ApsSemiFinishedGoodsProductionPlanService extends IService<ApsSemiFinishedGoodsProductionPlan> ,ApsSchedulingResuleBase{

    PageListRestVo<ApsSemiFinishedGoodsProductionPlan> getAllPage(Integer page, Integer size);
}
