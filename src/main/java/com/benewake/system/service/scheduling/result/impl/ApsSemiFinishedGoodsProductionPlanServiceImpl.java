package com.benewake.system.service.scheduling.result.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsSemiFinishedGoodsProductionPlan;
import com.benewake.system.entity.dto.ApsSemiFinishedGoodsProductionPlanDto;
import com.benewake.system.entity.enums.SchedulingResultType;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.mapper.ApsSemiFinishedGoodsProductionPlanMapper;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.scheduling.result.ApsSemiFinishedGoodsProductionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author ASUS
* @description 针对表【aps_semi_finished_goods_production_plan】的数据库操作Service实现
* @createDate 2023-11-02 11:38:43
*/
@Service
public class ApsSemiFinishedGoodsProductionPlanServiceImpl extends ServiceImpl<ApsSemiFinishedGoodsProductionPlanMapper, ApsSemiFinishedGoodsProductionPlan>
    implements ApsSemiFinishedGoodsProductionPlanService{


    @Autowired
    private ApsTableVersionService apsTableVersionService;

    @Autowired
    private ApsSemiFinishedGoodsProductionPlanMapper semiFinishedGoodsProductionPlanMapper;

    @Override
    public PageResultVo<ApsSemiFinishedGoodsProductionPlanDto> getAllPage(Integer page, Integer size) {
        Integer apsTableVersion = getApsTableVersion(SchedulingResultType.APS_SEMI_FINISHED_GOODS_PRODUCTION_PLAN.getCode(), apsTableVersionService);
        Page<ApsSemiFinishedGoodsProductionPlan> goodsProductionPlanPage = new Page<>();
        goodsProductionPlanPage.setSize(size);
        goodsProductionPlanPage.setCurrent(page);
        Page<ApsSemiFinishedGoodsProductionPlanDto> productionPlanPage = semiFinishedGoodsProductionPlanMapper.selectPageList(goodsProductionPlanPage, apsTableVersion);
        PageResultVo<ApsSemiFinishedGoodsProductionPlanDto> restVo = new PageResultVo<>();
        restVo.setList(productionPlanPage.getRecords());
        restVo.setPages(productionPlanPage.getPages());
        restVo.setTotal(productionPlanPage.getTotal());
        restVo.setPage(page);
        restVo.setSize(size);
        return restVo;
    }
}




