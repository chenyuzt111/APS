package com.benewake.system.controller;

import com.benewake.system.annotation.Scheduling;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.enums.InterfaceDataType;
import com.benewake.system.entity.enums.TableVersionState;
import com.benewake.system.entity.vo.ReturnTest;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsAllPlanNumInProcessService;
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

    @Autowired
    private ApsAllPlanNumInProcessService apsAllPlanNumInProcessService;


    @ApiOperation("数据库更新")
    @PostMapping("/dataUpdate")
    public Result dataUpdate(@RequestBody List<Integer> ids) throws Exception {
        long l = System.currentTimeMillis();
        if (CollectionUtils.isEmpty(ids)) {
            ids = InterfaceDataType.getAllIds();
        }

        interfaceDataService.updateData(ids);
        long l1 = System.currentTimeMillis();
        System.err.println(l1 -l);
        return Result.ok();
    }


    @ApiOperation("开始排程")
    @PostMapping("/startScheduling")
    public Result startScheduling() throws NoSuchFieldException, IllegalAccessException {
        pythonService.startScheduling();
        return Result.ok();
    }

    @ApiOperation("完整性检查")
    @PostMapping("/integrityChecker")
    public Result integrityChecker() {
        pythonService.integrityChecker();
        return Result.ok();
    }

    @ApiOperation(" ")
    @PostMapping("/getAllPlanNumInProcess")
    public Result<ReturnTest> all_plan_num_in_process() {
        ReturnTest latestCompletion = apsAllPlanNumInProcessService.getLatestCompletion();
        return Result.ok(latestCompletion);
    }

    //TODO 保存接口 只有排程完成才可以做保存 -》 先检查最新版本的数据的状态是否是排程完成 -》 修改该版本状态修改完已完成 -》 此时才可以解除用户级别的大锁
}
