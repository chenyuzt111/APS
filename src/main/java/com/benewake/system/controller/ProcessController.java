package com.benewake.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.benewake.system.entity.*;
import com.benewake.system.entity.vo.*;
import com.benewake.system.service.ApsFinishedProductBasicDataService;
import com.benewake.system.service.ApsProcessCapacityService;
import com.benewake.system.service.ApsProcessNamePoolService;
import com.benewake.system.service.ApsProcessSchemeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
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

    @ApiOperation("获取工序名称")
    @GetMapping("/getProcessNamePools")
    public Result getProcess(@RequestParam(required = false) String name) {
        LambdaQueryWrapper<ApsProcessNamePool> apsProcessNamePoolLambdaQueryWrapper = null;
        if (StringUtils.isNotBlank(name)) {
            apsProcessNamePoolLambdaQueryWrapper = new LambdaQueryWrapper<>();
            apsProcessNamePoolLambdaQueryWrapper.like(ApsProcessNamePool::getProcessName, name);
        }
        List<ApsProcessNamePool> apsProcessNamePools = apsProcessNamePoolService.getBaseMapper().selectList(apsProcessNamePoolLambdaQueryWrapper);
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
        return Result.ok(productFamily);
    }

    @ApiOperation("添加或修改工序与生产")
    @PostMapping("/addOrUpdateProcessCapacity")
    public Result addOrUpdateProcessCapacity(@RequestBody ApsProcessCapacityParam apsProcessCapacityVo) {
        if (apsProcessCapacityVo == null || StringUtils.isEmpty(apsProcessCapacityVo.getProcessName()) ||
                StringUtils.isEmpty(apsProcessCapacityVo.getBelongingProcess()) ||
                StringUtils.isEmpty(apsProcessCapacityVo.getPackagingMethod())) {
            return Result.fail("不能为空");
        }
        Boolean res = apsProcessCapacityService.saveOrUpdateProcessCapacityService(apsProcessCapacityVo);

        return res ? Result.ok() : Result.fail();
    }

    @ApiOperation("分页查工序与生产")
    @GetMapping("/getProcessCapacity/{page}/{size}")
    public Result getAllProcessCapacity(@PathVariable Integer page, @PathVariable Integer size) {
        ApsProcessCapacityListVo allProcessCapacity = apsProcessCapacityService.getAllProcessCapacity(page, size);
        return Result.ok(allProcessCapacity);
    }

    @ApiOperation("根据产品族查工序与生产")
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
        return  Result.ok(saveProcessScheme);
    }

    @ApiOperation("修改工序方案")
    @PostMapping("/updateProcessScheme")
    public Result updateProcessScheme(@RequestBody  List<ApsProcessSchemeParam> apsProcessSchemeParam) {
        if (CollectionUtils.isEmpty(apsProcessSchemeParam)) {
            return Result.fail("不能为null");
        }
        String saveProcessScheme = apsProcessSchemeService.updateProcessScheme(apsProcessSchemeParam);
        return  Result.ok(saveProcessScheme);
    }


    @ApiOperation("查询工序方案")
    @GetMapping("/getProcessScheme/{page}/{size}")
    public Result getProcessScheme(@PathVariable Integer page ,@PathVariable Integer size){
        List<ApsProcessSchemeVo> apsProcessSchemeVoList = apsProcessSchemeService.getProcessScheme(page ,size);
        return Result.ok(apsProcessSchemeVoList);
    }

    @ApiOperation("查询工序方案根据当前方案id")
    @GetMapping("/getProcessSchemeById")
    public Result getProcessSchemeById(@PathParam("id") Integer id){
        List<ApsProcessSchemeVo> apsProcessSchemeVoList = apsProcessSchemeService.getProcessSchemeById(id);
        return Result.ok(apsProcessSchemeVoList);
    }

    @ApiOperation("删除工序方案")
    @PostMapping("/deleteProcessScheme")
    public Result deleteProcessScheme(@RequestBody List<Integer> ids){
        Boolean res = apsProcessSchemeService.deleteProcessScheme(ids);
        return res ? Result.ok() : Result.fail();
    }
}
