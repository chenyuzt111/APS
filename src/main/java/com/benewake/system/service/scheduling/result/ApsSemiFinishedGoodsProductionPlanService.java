package com.benewake.system.service.scheduling.result;

import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.ApsSemiFinishedGoodsProductionPlan;
import com.benewake.system.entity.dto.ApsSemiFinishedGoodsProductionPlanDto;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ResultColPageVo;
import com.benewake.system.service.ApsTableVersionService;
import org.springframework.beans.factory.annotation.Autowired;

/**
* @author ASUS
* @description 针对表【aps_semi_finished_goods_production_plan】的数据库操作Service
* @createDate 2023-11-02 11:38:43
*/
public interface ApsSemiFinishedGoodsProductionPlanService extends IService<ApsSemiFinishedGoodsProductionPlan> ,ApsSchedulingResuleBase{

    PageResultVo<ApsSemiFinishedGoodsProductionPlanDto> getAllPage(Integer page, Integer size);

    ResultColPageVo<Object> semiFinishedGoodsFiltrate(Integer page, Integer size, QueryViewParams queryViewParams);

}
