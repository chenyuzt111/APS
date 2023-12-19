package com.benewake.system.controller;

import com.benewake.system.entity.Result;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ResultColPageVo;
import com.benewake.system.service.MasterDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "主数据")
@RestController
@RequestMapping("/masterData")
public class MasterDataController {

    @Autowired
    private MasterDataService masterDataService;

    @ApiOperation("筛选查询")
    @PostMapping("/get/{page}/{size}")
    public Result getFiltrateDate(@PathVariable("page") Integer page, @PathVariable("size") Integer size,
                                  @RequestBody(required = false) QueryViewParams queryViewParams) {
        ResultColPageVo<Object> res = masterDataService.getFiltrateDate(page, size, queryViewParams);
        return Result.ok(res);
    }

}
