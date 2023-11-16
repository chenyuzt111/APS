package com.benewake.system.controller;

import com.benewake.system.entity.*;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.vo.*;
import com.benewake.system.entity.vo.UpdateOptimalStrategyParam;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Api(tags = "工序与产能")
@RestController
@RequestMapping("/process")
public class ProcessController {

    @Autowired
    private ApsProcessNamePoolService apsProcessNamePoolService;

    @Autowired
    private ApsFinishedProductBasicDataService apsFinishedProductBasicDataService;

    @Autowired
    private ApsProcessCapacityService apsProcessCapacityService;

    @Autowired
    private ApsProcessSchemeService apsProcessSchemeService;

    @Autowired
    private ApsProductFamilyProcessSchemeManagementService apsProductFamilyProcessSchemeManagementService;

    @Autowired
    private ApsOptimalStrategyService apsOptimalStrategyService;


    @ApiOperation("添加或更新工序名称")
    @PostMapping("/updateProcessName")
    public Result addorUpdateProcess(@RequestBody ApsProcessNamePool apsProcessNamePool) {
        if (apsProcessNamePool == null || StringUtils.isEmpty(apsProcessNamePool.getProcessName())) {
            return Result.fail("工序名称不能为null");
        }
        return apsProcessNamePoolService.addOrUpdateProcess(apsProcessNamePool) ? Result.ok() : Result.fail();
    }

    @ApiOperation("删除工序名称")
    @PostMapping("/deleteProcessName")
    public Result deleteProcess(@RequestBody List<Integer> ids) {
        return apsProcessNamePoolService.removeBatchByIds(ids) ? Result.ok() : Result.fail();
    }

    @ApiOperation("获取工序名称分页")
    @GetMapping("/getProcessNamePools/{page}/{size}")
    public Result getProcess(@RequestParam(required = false) String name, @PathVariable Integer page, @PathVariable Integer size) {
        ApsProcessNamePoolPageVo apsProcessNamePoolVo = apsProcessNamePoolService.getProcess(name, page, size);
        return Result.ok(apsProcessNamePoolVo);
    }

    @ApiOperation("获取工序名称")
    @GetMapping("/getProcessNamePools")
    public Result getProcess(@RequestParam(required = false) String name) {
        List<ApsProcessNamePool> apsProcessNamePools = apsProcessNamePoolService.getBaseMapper().selectList(null);
        return Result.ok(apsProcessNamePools);
    }

    @ApiOperation("产品族")
    @GetMapping("/getAllProductFamily")
    public Result getAllProductFamily() {
        List<ApsFinishedProductBasicData> apsFinishedProductBasicData = apsFinishedProductBasicDataService.getBaseMapper().selectList(null);
        List<String> productFamily = apsFinishedProductBasicData.stream().map(ApsFinishedProductBasicData::getFProductFamily).distinct().collect(Collectors.toList());
        return Result.ok(productFamily);
    }

    @ApiOperation("包装类型")
    @GetMapping("/getAllPackagingMethod")
    public Result getAllPackagingMethod() {
        List<ApsFinishedProductBasicData> apsFinishedProductBasicData = apsFinishedProductBasicDataService.getBaseMapper().selectList(null);
        List<String> productFamily = apsFinishedProductBasicData.stream().map(ApsFinishedProductBasicData::getFPackagingMethod).distinct().collect(Collectors.toList());
        productFamily.add("空");
        return Result.ok(productFamily);
    }

    @ApiOperation("添加或修改工序与生产")
    @PostMapping("/addOrUpdateProcessCapacity")
    public Result addOrUpdateProcessCapacity(@RequestBody ApsProcessCapacityParam apsProcessCapacityVo) {
        if (apsProcessCapacityVo == null || StringUtils.isEmpty(apsProcessCapacityVo.getProcessName()) ||
                StringUtils.isEmpty(apsProcessCapacityVo.getBelongingProcess())) {
            return Result.fail("不能为空");
        }
        if (apsProcessCapacityVo.getPackagingMethod() != null && apsProcessCapacityVo.getPackagingMethod().equals("空")) {
            apsProcessCapacityVo.setPackagingMethod("");
        }
        Boolean res = apsProcessCapacityService.saveOrUpdateProcessCapacityService(apsProcessCapacityVo);
        return res ? Result.ok() : Result.fail();
    }

    @ApiOperation("批量修改修改工序与生产")
    @PostMapping("/updateProcessNumber")
    public Result updateProcessNumber(@RequestBody List<ApsProcessCapacityParam> apsProcessCapacityVo) {
        if (CollectionUtils.isEmpty(apsProcessCapacityVo)) {
            return Result.fail("不能为空");
        }
        Boolean res = apsProcessCapacityService.updateProcessNumber(apsProcessCapacityVo);
        return res ? Result.ok() : Result.fail();
    }

    @ApiOperation("分页查工序与生产")
    @GetMapping("/getProcessCapacity/{page}/{size}")
    public Result getAllProcessCapacity(@PathVariable Integer page, @PathVariable Integer size) {
        ApsProcessCapacityListVo allProcessCapacity = apsProcessCapacityService.getAllProcessCapacity(page, size);
        return Result.ok(allProcessCapacity);
    }

    @ApiOperation("根据产品族查工序与生产过滤掉了所属工序不是组装的")
    @GetMapping("/getProcessCapacityByProductFamily")
    public Result getAllProcessCapacity(@PathParam("productFamily") String productFamily) {
        List<ApsProcessCapacityVo> allProcessCapacity = apsProcessCapacityService.getProcessCapacitysByproductFamily(productFamily);
        return CollectionUtils.isNotEmpty(allProcessCapacity) ? Result.ok(allProcessCapacity) : Result.fail("当前产品族没有创建工序与产能");
    }

    @ApiOperation("删除工序与生产数据（真删奥）")
    @PostMapping("/deleteProcessCapacity")
    public Result deleteProcessCapacity(@RequestBody List<Integer> ids) {
        return apsProcessCapacityService.removeBatchAndUpdateByIds(ids) ? Result.ok() : Result.fail();
    }

    @ApiOperation("保存工序方案")
    @PostMapping("/saveProcessScheme")
    public Result saveProcessScheme(@RequestBody ApsProcessSchemeParams apsProcessSchemeParams) {
        if (apsProcessSchemeParams == null ||
                CollectionUtils.isEmpty(apsProcessSchemeParams.getApsProcessSchemeParam()) ||
                apsProcessSchemeParams.getNumber() == null) {
            return Result.fail("不能为null");
        }
        String saveProcessScheme = apsProcessSchemeService.saveProcessScheme(apsProcessSchemeParams);
        return Result.ok(saveProcessScheme);
    }

    @ApiOperation("修改工序方案")
    @PostMapping("/updateProcessScheme")
    public Result updateProcessScheme(@RequestBody List<ApsProcessSchemeParam> apsProcessSchemeParam) {
        if (CollectionUtils.isEmpty(apsProcessSchemeParam)) {
            return Result.fail("不能为null");
        }
        Boolean saveProcessScheme = apsProcessSchemeService.updateProcessScheme(apsProcessSchemeParam);
        return saveProcessScheme ? Result.ok() : Result.fail();
    }


    @ApiOperation("查询基础工序方案")
    @GetMapping("/getProcessScheme/{page}/{size}")
    public Result getProcessScheme(@PathVariable Integer page, @PathVariable Integer size) {
        ApsProcessSchemeVoPage apsProcessSchemeVoList = apsProcessSchemeService.getProcessScheme(page, size);
        return Result.ok(apsProcessSchemeVoList);
    }

    @ApiOperation("查询工序方案根据当前方案id")
    @GetMapping("/getProcessSchemeById")
    public Result getProcessSchemeById(@PathParam("id") Integer id) {
        ApsProcessSchemeByIdListVo apsProcessSchemeVoList = apsProcessSchemeService.getProcessSchemeById(id);
        return Result.ok(apsProcessSchemeVoList);
    }

    @ApiOperation("删除工序方案")
    @PostMapping("/deleteProcessScheme")
    public Result deleteProcessScheme(@RequestBody List<Integer> ids) {
        Boolean res = apsProcessSchemeService.deleteProcessScheme(ids);
        return res ? Result.ok() : Result.fail();
    }

    @ApiOperation("最终工艺方案")
    @GetMapping("/getProcessSchemeManagement/{page}/{size}")
    public Result getProcessSchemeManagement(@PathVariable Integer page, @PathVariable Integer size) {
        ProcessSchemeManagementVoPage res = apsProductFamilyProcessSchemeManagementService.getProcessSchemeManagement(page, size);
        return Result.ok(res);
    }

    @ApiOperation("设置经济批量")
    @PostMapping("/updateProcessSchemeManagement")
    public Result updateProcessSchemeManagement(@RequestBody ProcessSchemeManagementParam processSchemeManagementParam) {
        if (processSchemeManagementParam == null) {
            return Result.fail("不能为null");
        }
        Boolean saveProcessScheme = apsProductFamilyProcessSchemeManagementService.setOrderNumber(processSchemeManagementParam);
        return Result.ok(saveProcessScheme);
    }

    @ApiOperation("修改优先策略")
    @PostMapping("/updateOptimalStrategy")
    public Result updateOptimalStrategy(@RequestBody UpdateOptimalStrategyParam updateOptimalStrategyParam) {
        Boolean res = apsOptimalStrategyService.updateAndOptimalProcess(updateOptimalStrategyParam);
        return Result.ok(res);
    }

    @ApiOperation("导出工序命名池数据")
    @PostMapping("/downloadProceeName")
    public void downloadProceeName(@RequestBody DownloadParam downloadParam, HttpServletResponse response) {
        if (downloadParam == null || downloadParam.getType() == null
                || (downloadParam.getType() == ExcelOperationEnum.CURRENT_PAGE.getCode()
                && (downloadParam.getPage() == null || downloadParam.getSize() == null))) {
            throw new BeneWakeException("数据不正确");
        }
        apsProcessNamePoolService.downloadProceeName(response, downloadParam);
    }

    @ApiOperation("导入工序命名池数据")
    @PostMapping("/importProceeName")
    public Result importProceeName(@PathParam("type") Integer type, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("文件为空！");
        }
        String[] split = file.getOriginalFilename().split("\\.");
        if (!"xlsx".equals(split[1]) && !"xls".equals(split[1])) {
            return Result.fail("请提供.xlsx或.xls为后缀的Excel文件");
        }
        Boolean res = apsProcessNamePoolService.saveDataByExcel(type, file);
        return res ? Result.ok() : Result.fail();
    }



    @ApiOperation("导出工序与产能")
    @PostMapping("/downloadProcessCapacity")
    public void downloadProcessCapacity(@RequestBody DownloadParam downloadParam, HttpServletResponse response) {
        if (downloadParam == null || downloadParam.getType() == null
                || (downloadParam.getType() == ExcelOperationEnum.CURRENT_PAGE.getCode()
                && (downloadParam.getPage() == null || downloadParam.getSize() == null))) {
            throw new BeneWakeException("数据不正确");
        }
        apsProcessCapacityService.downloadProcessCapacity(response, downloadParam);
    }

    @ApiOperation("导入工序与产能")
    @PostMapping("/importProcessCapacity")
    public Result importProcessCapacity(@PathParam("type") Integer type, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("文件为空！");
        }
        String[] split = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
        if (!"xlsx".equals(split[1]) && !"xls".equals(split[1])) {
            return Result.fail("请提供.xlsx或.xls为后缀的Excel文件");
        }
        Boolean res = apsProcessCapacityService.saveDataByExcel(type, file);
        return res ? Result.ok() : Result.fail();
    }

    @ApiOperation("导出基础工艺方案")
    @PostMapping("/downloadProcessScheme")
    public void downloadProcessScheme(@RequestBody DownloadParam downloadParam, HttpServletResponse response) {
        if (downloadParam == null || downloadParam.getType() == null
                || (downloadParam.getType() == ExcelOperationEnum.CURRENT_PAGE.getCode()
                && (downloadParam.getPage() == null || downloadParam.getSize() == null))) {
            throw new BeneWakeException("数据不正确");
        }
        apsProcessSchemeService.downloadProcessCapacity(response, downloadParam);
    }
}
