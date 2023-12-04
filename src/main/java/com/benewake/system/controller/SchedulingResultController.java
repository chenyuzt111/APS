package com.benewake.system.controller;


import com.benewake.system.entity.Result;
import com.benewake.system.entity.dto.*;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.vo.DownloadParam;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.scheduling.result.*;
import com.benewake.system.service.scheduling.result.impl.ApsAllPlanNumInProcessServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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

        PageResultVo<ApsProductionPlanDto> apsProductionPlans = apsProductionPlanService.getAllPage(page, size);
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
        PageResultVo<ApsSemiFinishedGoodsProductionPlanDto> apsProductionPlans = apsSemiFinishedGoodsProductionPlanService.getAllPage(page, size);
        return Result.ok(apsProductionPlans);
    }

    @ApiOperation("成品缺料分析分页")
    @GetMapping("/getMaterialShortageAnalysis/{page}/{size}")
    public Result getMaterialShortageAnalysis(@PathVariable Integer page, @PathVariable Integer size) {
        PageResultVo<ApsMaterialShortageAnalysisDto> apsProductionPlans = apsMaterialShortageAnalysisService.getAllPage(page, size);
        return Result.ok(apsProductionPlans);
    }

    @ApiOperation("半成品缺料分析分页")
    @GetMapping("/getSemiFinishedGoodsMaterialShortageAnalysis/{page}/{size}")
    public Result getSemiFinishedGoodsMaterialShortageAnalysis(@PathVariable Integer page, @PathVariable Integer size) {
        PageResultVo<ApsSemiFinishedGoodsMaterialShortageAnalysisDto> res = apsSemiFinishedGoodsMaterialShortageAnalysisService.getAllPage(page, size);
        return Result.ok(res);
    }

    @ApiOperation("FIM需求优先级")
    @GetMapping("/getFimPriority/{page}/{size}")
    public Result getFimPriority(@PathVariable Integer page, @PathVariable Integer size) {
        PageResultVo<ApsFimPriorityDto> res = apsFimPriorityService.getAllPage(page, size);
        return Result.ok(res);
    }


    @ApiOperation("导出FIM需求优先级")
    @PostMapping("/downloadFimPriority")
    public void downloadFimPriority(@RequestBody DownloadParam downloadParam,  HttpServletResponse response) {
        if (downloadParam == null || downloadParam.getType() == null
                || (downloadParam.getType() == ExcelOperationEnum.CURRENT_PAGE.getCode()
                && (downloadParam.getPage() == null || downloadParam.getSize() == null))) {
            throw new BeneWakeException("数据不正确");
        }
        apsFimPriorityService.downloadFimRequest(response, downloadParam);
    }
}
