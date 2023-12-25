package com.benewake.system.controller;


import com.benewake.system.annotation.SearchHistory;
import com.benewake.system.annotation.SearchLike;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.enums.SchedulingResultType;
import com.benewake.system.entity.vo.*;
import com.benewake.system.entity.vo.baseParam.SearchLikeParam;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.SchedulingResultService;
import com.benewake.system.service.scheduling.result.*;
import com.benewake.system.service.scheduling.result.impl.ApsAllPlanNumInProcessServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "排程结果 及甘特图")
@RestController
@RequestMapping("/scResult")
@Slf4j
public class SchedulingResultController {

    @Autowired
    private ApsProductionPlanService apsProductionPlanService;

    @Autowired
    private ApsAllPlanNumInProcessService apsAllPlanNumInProcessService;

    @Autowired
    private ApsSemiFinishedGoodsProductionPlanService semiFinishedGoodsProductionPlanService;

    @Autowired
    private ApsSemiFinishedGoodsMaterialShortageAnalysisService apsSemiFinishedGoodsMaterialShortageAnalysisService;

    @Autowired
    private ApsMaterialShortageAnalysisService apsMaterialShortageAnalysisService;

    @Autowired
    private ApsFimPriorityService apsFimPriorityService;

    @Autowired
    private SchedulingResultService schedulingResultService;

//    @SearchHistory
    @ApiOperation("成品生产结果报表筛选")
    @PostMapping("/getProductionFiltrate/{page}/{size}")
    public Result getProductionFiltrate(@PathVariable Integer page, @PathVariable Integer size, @RequestBody(required = false) QueryViewParams queryViewParams) {
        queryViewParams.setTableId(SchedulingResultType.APS_PRODUCTION_PLAN.getCode());
        ResultColPageVo<Object> apsProductionPlans = apsProductionPlanService.getResultFiltrate(page, size, queryViewParams);
        return Result.ok(apsProductionPlans);
    }

    @ApiOperation("成品生产结果甘特图")
    @GetMapping("/getProductionPlanSort")
    public Result getProductionPlanSort() {
        List<ApsAllPlanNumInProcessServiceImpl.Item> productionPlanSort = apsAllPlanNumInProcessService.getProductionPlanSort();
        return Result.ok(productionPlanSort);
    }


//    @SearchHistory
    @ApiOperation("半成品生产计划分页筛选")
    @PostMapping("/semiFinishedGoodsFiltrate/{page}/{size}")
    public Result semiFinishedGoodsFiltrate(@PathVariable Integer page, @PathVariable Integer size, @RequestBody(required = false) QueryViewParams queryViewParams) {
        queryViewParams.setTableId(SchedulingResultType.APS_SEMI_FINISHED_GOODS_PRODUCTION_PLAN.getCode());
        ResultColPageVo<Object> apsProductionPlans = semiFinishedGoodsProductionPlanService.getResultFiltrate(page, size, queryViewParams);
        return Result.ok(apsProductionPlans);
    }


//    @SearchHistory
    @ApiOperation("成品缺料分析分页筛选")
    @PostMapping("/materialShortageAnalysisFiltrate/{page}/{size}")
    public Result materialShortageAnalysisFiltrate(@PathVariable Integer page, @PathVariable Integer size, @RequestBody(required = false) QueryViewParams queryViewParams) {
        queryViewParams.setTableId(SchedulingResultType.APS_MATERIAL_SHORTAGE_ANALYSIS.getCode());
        ResultColPageVo<Object> apsProductionPlans = apsMaterialShortageAnalysisService.getResultFiltrate(page, size, queryViewParams);
        return Result.ok(apsProductionPlans);
    }

//    @SearchHistory
    @ApiOperation("半成品缺料分析分页筛选")
    @PostMapping("/semiMaterialShortageFiltrate/{page}/{size}")
    public Result semiMaterialShortageFiltrate(@PathVariable Integer page, @PathVariable Integer size, @RequestBody(required = false) QueryViewParams queryViewParams) {
        queryViewParams.setTableId(SchedulingResultType.APS_SEMI_FINISHED_GOODS_MATERIAL_SHORTAGE_ANALYSIS.getCode());
        ResultColPageVo<Object> res = apsSemiFinishedGoodsMaterialShortageAnalysisService
                .getResultFiltrate(page, size, queryViewParams);
        return Result.ok(res);
    }
//    @SearchHistory
    @ApiOperation("FIM需求优先级筛选")
    @PostMapping("/getFimPriorityFiltrate/{page}/{size}")
    public Result getFimPriorityFiltrate(@PathVariable Integer page, @PathVariable Integer size, @RequestBody(required = false) QueryViewParams queryViewParams) {
        queryViewParams.setTableId(SchedulingResultType.APS_FIM_PRIORITY.getCode());
        ResultColPageVo<Object> res = apsFimPriorityService.getResultFiltrate(page, size ,queryViewParams);
        return Result.ok(res);
    }

    @ApiOperation("导出排程结果数据")
    @PostMapping("/download")
    public void download(@RequestBody DownloadViewParams downloadParam, HttpServletResponse response) {
        if (downloadParam == null || downloadParam.getTableId() == null
                || (downloadParam.getType() == ExcelOperationEnum.CURRENT_PAGE.getCode()
                && (downloadParam.getPage() == null || downloadParam.getSize() == null))) {
            throw new BeneWakeException("数据不正确");
        }
        schedulingResultService.download(response, downloadParam);
    }
//    @SearchLike
    @ApiOperation("搜索筛选补全")
    @PostMapping("searchLike")
    public Result searchLike(@RequestBody SearchLikeParam searchLikeParam) {
        long l = System.currentTimeMillis();
        List<Object> res = schedulingResultService.searchLike(searchLikeParam);
        long l1 = System.currentTimeMillis();
        log.info("搜索耗时：" + (l1 - l));
        return Result.ok(res);
    }
}
