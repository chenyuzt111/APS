package com.benewake.system.controller;


import com.benewake.system.entity.ApsProductFamilyMachineTable;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.vo.ApsProductFamilyMachineTablePageVo;
import com.benewake.system.entity.vo.ApsProductFamilyMachineTableParam;
import com.benewake.system.entity.vo.DownloadParam;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsProductFamilyMachineTableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "机器管理")
@RestController
@RequestMapping("/machine")
public class MachineController {

    @Autowired
    private ApsProductFamilyMachineTableService apsProductFamilyMachineTableService;

    @ApiOperation("获取机器管理")
    @GetMapping("/getApsMachineTable/{page}/{size}")
    public Result<ApsProductFamilyMachineTablePageVo> getApsMachineTable(@RequestParam(required = false) String name, @PathVariable Integer page, @PathVariable Integer size) {
        ApsProductFamilyMachineTablePageVo apsProductFamilyMachineTablePageVo =
                apsProductFamilyMachineTableService.getApsMachineTable(name, page ,size);
        return Result.ok(apsProductFamilyMachineTablePageVo);
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

    @ApiOperation("新增或修改机器管理")
    @PostMapping("/addOrUpdateApsMachineTable")
    public Result addOrUpdateApsMachineTable(@RequestBody ApsProductFamilyMachineTableParam apsProductFamilyMachineTable) {
        if (apsProductFamilyMachineTable == null) {
            return Result.fail();
        }
        boolean res = apsProductFamilyMachineTableService.addOrUpdateApsMachineTable(apsProductFamilyMachineTable);
        return Result.ok(res);
    }

    @ApiOperation("导出机器管理列表")
    @PostMapping("/downloadApsMachineTable")
    public void downloadSchemeManagement(@RequestBody DownloadParam downloadParam, HttpServletResponse response) {
        if (downloadParam == null || downloadParam.getType() == null
                || (downloadParam.getType() == ExcelOperationEnum.CURRENT_PAGE.getCode()
                && (downloadParam.getPage() == null || downloadParam.getSize() == null))) {
            throw new BeneWakeException("数据不正确");
        }
        apsProductFamilyMachineTableService.downloadProcessCapacity(response, downloadParam);
    }


//    @ApiOperation("下载导入模板")
//    @PostMapping("/downloadMachineTable")



}
