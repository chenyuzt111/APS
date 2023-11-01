package com.benewake.system.controller;


import com.benewake.system.entity.ApsProductionPlan;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.vo.PageListRestVo;
import com.benewake.system.service.ApsAllPlanNumInProcessService;
import com.benewake.system.service.ApsProductionPlanService;
import com.benewake.system.service.impl.ApsAllPlanNumInProcessServiceImpl;
import com.benewake.system.service.impl.ApsProductionPlanServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.Data;
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

    @ApiOperation("成品生产结果报表分页")
    @GetMapping("/getProductionPlan/{page}/{size}")
    public Result getProductionPlan(@PathVariable Integer page, @PathVariable Integer size) {
        PageListRestVo<ApsProductionPlan> apsProductionPlans = apsProductionPlanService.getAllPage(page ,size);
        return Result.ok(apsProductionPlans);
    }


    @ApiOperation("测试")
    @GetMapping("/getProductionPlanSort")
    public Result getProductionPlanSort() {
        List<ApsAllPlanNumInProcessServiceImpl.Item> productionPlanSort = apsAllPlanNumInProcessService.getProductionPlanSort();
        return Result.ok(null);
    }


}
