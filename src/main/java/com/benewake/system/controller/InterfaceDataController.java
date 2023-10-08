package com.benewake.system.controller;

import com.benewake.system.entity.ApsTableVersion;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.enums.TableVersionState;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.InterfaceDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 接口数据
 *
 * @author wangxuyue
 * @since 2023-09-27
 */
@Api(tags = "接口数据")
@RestController
@RequestMapping("/interfacedata")
public class InterfaceDataController {

    @Autowired
    private InterfaceDataService interfaceDataService;

    @Autowired
    private ApsTableVersionService apsTableVersionService;

    @ApiOperation("刷新接口数据")
    @PostMapping("/update")
    public Result update(@RequestBody List<Integer> ids) throws Exception {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BeneWakeException("ids不能为空");
        }
        interfaceDataService.updateData(ids);
        return Result.ok();
    }

    @ApiOperation("获取最新数据库版本状态")
    @GetMapping("/getMaxTableVersionState")
    public Result getTableVersionState(){
        Integer state = apsTableVersionService.getMaxVersionState();
        if (state != TableVersionState.EDITING.getCode()) {
            return Result.ok();
        }
        return Result.fail("当前存在证正在编辑的版本是否要更新");
    }
}
