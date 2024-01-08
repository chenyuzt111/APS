package com.benewake.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.benewake.system.entity.*;
import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ViewParam;
import com.benewake.system.entity.vo.ViewTableListVo;
import com.benewake.system.service.ApsColumnTableService;
import com.benewake.system.service.ApsConditionTableService;
import com.benewake.system.service.ApsViewColTableService;
import com.benewake.system.service.ApsViewTableService;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.python.modules._py_compile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Api(tags = "视图管理")
@RestController
@RequestMapping("/filtrate")
public class FiltrateController {

    @Autowired
    private ApsViewTableService viewTableService;

    @Autowired
    private ApsColumnTableService columnTableService;

    @Autowired
    private ApsViewColTableService viewColTableService;

    @Autowired
    private ApsConditionTableService conditionTableService;

    @ApiOperation("查询视图")
    @GetMapping("/getViews/{tableId}")
    public Result getViews(@PathVariable("tableId") Integer tableId) {
        ViewTableListVo viewTables = viewTableService.getViews(tableId);
        return Result.ok(viewTables);
    }

    @ApiOperation("保存或修改视图")
    @PostMapping("/saveView")
    public Result saveView(@RequestBody ViewParam viewParam) {
        if (viewParam == null || viewParam.getTableId() == null) {
            return Result.fail();
        }
        Boolean res = viewTableService.saveView(viewParam);
        return res ? Result.ok() : Result.fail();
    }

    @ApiOperation("删除视图")
    @PostMapping("/removeView")
    public Result removeView(@RequestBody List<Integer> ids) {
        List<ApsViewColTable> list = viewColTableService.list(new LambdaQueryWrapper<ApsViewColTable>()
                .in(ApsViewColTable::getViewId, ids));
        conditionTableService.remove(new LambdaQueryWrapper<ApsConditionTable>()
                .in(ApsConditionTable::getViewColId, list.stream().map(ApsViewColTable::getId).collect(Collectors.toList())));
        viewColTableService.remove(new LambdaQueryWrapper<ApsViewColTable>()
                .in(ApsViewColTable::getViewId, ids));
        boolean res = viewTableService.removeBatchByIds(ids);
        return res ? Result.ok() : Result.fail();
    }

    @ApiOperation("显示当前所有列")
    @GetMapping("/getCols/{tableId}")
    public Result getCols(@PathVariable("tableId") Integer tableId) {
        List<ApsColumnTable> apsColumnTables = columnTableService
                .list(new LambdaQueryWrapper<ApsColumnTable>()
                        .eq(ApsColumnTable::getTableId, tableId)
                        .orderByAsc(ApsColumnTable::getSeq));
        return Result.ok(apsColumnTables);
    }


}