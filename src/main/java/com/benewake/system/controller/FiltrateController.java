package com.benewake.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.benewake.system.entity.ApsColumnTable;
import com.benewake.system.entity.ApsViewColTable;
import com.benewake.system.entity.ApsViewTable;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.vo.ViewParam;
import com.benewake.system.entity.vo.ViewTableListVo;
import com.benewake.system.service.ApsColumnTableService;
import com.benewake.system.service.ApsViewColTableService;
import com.benewake.system.service.ApsViewTableService;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/filtrate")
public class FiltrateController {

    @Autowired
    private ApsViewTableService viewTableService;

    @Autowired
    private ApsColumnTableService columnTableService;

    @Autowired
    private ApsViewColTableService viewColTableService;

    @ApiOperation("查询视图")
    @GetMapping("/getViews/{tableId}")
    public Result getViews(@PathVariable("tableId") Integer tableId) {
        ViewTableListVo viewTables = viewTableService.getViews(tableId);
        return Result.ok(viewTables);
    }

    @ApiOperation("保存或修改视图")
    @PostMapping("/saveView")
    public Result saveView(@RequestBody ViewParam viewParam) {
        if (viewParam == null || viewParam.getTableId() == null || CollectionUtils.isEmpty(viewParam.getCols())) {
            return Result.fail();
        }
        Boolean res = viewTableService.saveView(viewParam);
        return res ? Result.ok() : Result.fail();
    }

    @ApiOperation("删除视图")
    @PostMapping("/removeView")
    public Result removeView(@RequestBody List<Integer> ids) {
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
                        .eq(ApsColumnTable::getTableId, tableId));
        return Result.ok(apsColumnTables);
    }
}