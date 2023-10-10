package com.benewake.system.controller;

import com.benewake.system.entity.Result;
import com.benewake.system.entity.enums.TableVersionState;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.InterfaceDataService;
import com.benewake.system.service.PythonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "排程")
@RestController
@RequestMapping("/scheduling")
public class SchedulingController {

    @Autowired
    private InterfaceDataService interfaceDataService;

    @Autowired
    private ApsTableVersionService apsTableVersionService;

    @Autowired
    private PythonService pythonService;

    @ApiOperation("数据库更新")
    @PostMapping("/dataUpdate")
    public Result dataUpdate(@RequestBody List<Integer> ids) throws Exception {
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


    @ApiOperation("开始排程")
    @PostMapping("/startScheduling")
    public Result startScheduling(){
        pythonService.startScheduling();
        return Result.ok();
    }

    @ApiOperation("完整性检查")
    @PostMapping("/integrityChecker")
    public Result integrityChecker(){
        pythonService.integrityChecker();
        return Result.ok();
    }
}
