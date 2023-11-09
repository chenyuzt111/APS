package com.benewake.system.controller;


import com.benewake.system.entity.ApsProductFamilyMachineTable;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.vo.ApsProductFamilyMachineTableVo;
import com.benewake.system.service.ApsProductFamilyMachineTableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Api(tags = "机器管理")
@RestController
@RequestMapping("/machine")
public class MachineController {

    @Autowired
    private ApsProductFamilyMachineTableService apsProductFamilyMachineTableService;

    @ApiOperation("获取机器管理")
    @GetMapping("/getApsMachineTable/{page}/{size}")
    public Result<ApsProductFamilyMachineTableVo> getApsMachineTable(@RequestParam(required = false) String name, @PathVariable Integer page, @PathVariable Integer size) {
        ApsProductFamilyMachineTableVo apsProductFamilyMachineTableVo =
                apsProductFamilyMachineTableService.getApsMachineTable(name, page ,size);
        return Result.ok(apsProductFamilyMachineTableVo);
    }

    @ApiOperation("删除机器管理")
    @PostMapping("/deleteApsMachineTable")
    public Result deleteApsMachineTable(@RequestBody List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Result.fail();
        }
        boolean removeBatchByIds = apsProductFamilyMachineTableService.removeBatchByIds(ids);
        return Result.ok(removeBatchByIds);
    }

    @ApiOperation("修改机器管理")
    @PostMapping("/addOrUpdateApsMachineTable")
    public Result addOrUpdateApsMachineTable(@RequestBody ApsProductFamilyMachineTable apsProductFamilyMachineTable) {
        if (apsProductFamilyMachineTable == null) {
            return Result.fail();
        }
        boolean res = apsProductFamilyMachineTableService.addOrUpdateApsMachineTable(apsProductFamilyMachineTable);
        return Result.ok(res);
    }


}
