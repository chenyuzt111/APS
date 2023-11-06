package com.benewake.system.controller;


import com.benewake.system.entity.*;
import com.benewake.system.entity.vo.PageListRestVo;
import com.benewake.system.service.scheduling.result.*;
import com.benewake.system.service.scheduling.result.impl.ApsAllPlanNumInProcessServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "排程结果 及甘特图")
@RestController
@RequestMapping("/scResult")
public class SchedulingResultController {

    @Autowired
    private ApsProductionPlanService apsProductionPlanService;

    @Autowired
    private ApsAllPlanNumInProcessService apsAllPlanNumInProcessService;

    @Autowired
    private ApsSemiFinishedGoodsProductionPlanService apsSemiFinishedGoodsProductionPlanService;

    @Autowired
    private ApsSemiFinishedGoodsMaterialShortageAnalysisService apsSemiFinishedGoodsMaterialShortageAnalysisService;

    @Autowired
    private ApsMaterialShortageAnalysisService apsMaterialShortageAnalysisService;

    @Autowired
    private ApsFimPriorityService apsFimPriorityService;

    @ApiOperation("成品生产结果报表分页")
    @GetMapping("/getProductionPlan/{page}/{size}")
    public Result getProductionPlan(@PathVariable Integer page, @PathVariable Integer size) {
        PageListRestVo<ApsProductionPlan> apsProductionPlans = apsProductionPlanService.getAllPage(page, size);
        return Result.ok(apsProductionPlans);
    }


    @ApiOperation("成品生产结果甘特图")
    @GetMapping("/getProductionPlanSort")
    public Result getProductionPlanSort() {
        List<ApsAllPlanNumInProcessServiceImpl.Item> productionPlanSort = apsAllPlanNumInProcessService.getProductionPlanSort();
        return Result.ok(productionPlanSort);
    }

    @ApiOperation("半成品生产计划分页")
    @GetMapping("/getSemiFinishedGoodsProductionPlan/{page}/{size}")
    public Result getSemiFinishedGoodsProductionPlan(@PathVariable Integer page, @PathVariable Integer size) {
        PageListRestVo<ApsSemiFinishedGoodsProductionPlan> apsProductionPlans = apsSemiFinishedGoodsProductionPlanService.getAllPage(page, size);
        return Result.ok(apsProductionPlans);
    }


    @ApiOperation("成品缺料分析分页")
    @GetMapping("/getMaterialShortageAnalysis/{page}/{size}")
    public Result getMaterialShortageAnalysis(@PathVariable Integer page, @PathVariable Integer size) {
        PageListRestVo<ApsMaterialShortageAnalysis> apsProductionPlans = apsMaterialShortageAnalysisService.getAllPage(page, size);
        return Result.ok(apsProductionPlans);
    }

    @ApiOperation("半成品缺料分析分页")
    @GetMapping("/getSemiFinishedGoodsMaterialShortageAnalysis/{page}/{size}")
    public Result getSemiFinishedGoodsMaterialShortageAnalysis(@PathVariable Integer page, @PathVariable Integer size) {
        PageListRestVo<ApsSemiFinishedGoodsMaterialShortageAnalysis> res = apsSemiFinishedGoodsMaterialShortageAnalysisService.getAllPage(page, size);
        return Result.ok(res);
    }

    @ApiOperation("FIM需求优先级")
    @GetMapping("/getFimPriority/{page}/{size}")
    public Result getFimPriority(@PathVariable Integer page, @PathVariable Integer size) {
        PageListRestVo<ApsFimPriority> res = apsFimPriorityService.getAllPage(page, size);
        return Result.ok(res);
    }

}
