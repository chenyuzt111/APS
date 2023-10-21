package com.benewake.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsFinishedProductBasicData;
import com.benewake.system.entity.ApsProcessCapacity;
import com.benewake.system.entity.ApsProcessNamePool;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.vo.ApsProcessCapacityListVo;
import com.benewake.system.entity.vo.ApsProcessCapacityVo;
import com.benewake.system.service.ApsFinishedProductBasicDataService;
import com.benewake.system.service.ApsProcessCapacityService;
import com.benewake.system.service.ApsProcessNamePoolService;
import com.benewake.system.transfer.ApsProcessCapacityEntityToVo;
import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.math.BigDecimal;
import java.util.HashMap;
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



    @PostMapping("/updateProcessName")
    public Result addorUpdateProcess(@RequestBody ApsProcessNamePool apsProcessNamePool) {
        if (apsProcessNamePool == null || StringUtils.isEmpty(apsProcessNamePool.getProcessName())) {
            return Result.fail("工序名称不能为null");
        }
        return apsProcessNamePoolService.addOrUpdateProcess(apsProcessNamePool) ? Result.ok() : Result.fail();
    }

    @PostMapping("/deleteProcessName")
    public Result deleteProcess(@RequestBody List<Integer> ids) {
        return apsProcessNamePoolService.removeBatchByIds(ids) ? Result.ok() : Result.fail();
    }

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


    @GetMapping("/getAllProductFamily")
    public Result getAllProductFamily() {
        List<ApsFinishedProductBasicData> apsFinishedProductBasicData = apsFinishedProductBasicDataService.getBaseMapper().selectList(null);
        List<String> productFamily = apsFinishedProductBasicData.stream().map(ApsFinishedProductBasicData::getFProductFamily).distinct().collect(Collectors.toList());
        return Result.ok(productFamily);
    }


    @GetMapping("/getAllPackagingMethod")
    public Result getAllPackagingMethod() {
        List<ApsFinishedProductBasicData> apsFinishedProductBasicData = apsFinishedProductBasicDataService.getBaseMapper().selectList(null);
        List<String> productFamily = apsFinishedProductBasicData.stream().map(ApsFinishedProductBasicData::getFPackagingMethod).distinct().collect(Collectors.toList());
        return Result.ok(productFamily);
    }

    @PostMapping("/addOrUpdateProcessCapacity")
    public Result addOrUpdateProcessCapacity(@RequestBody ApsProcessCapacityVo apsProcessCapacityVo) {
        if (apsProcessCapacityVo == null || StringUtils.isEmpty(apsProcessCapacityVo.getProcessName()) ||
                StringUtils.isEmpty(apsProcessCapacityVo.getBelongingProcess()) ||
                StringUtils.isEmpty(apsProcessCapacityVo.getPackagingMethod())) {
            return Result.fail("不能为空");
        }
        Boolean res = apsProcessCapacityService.saveOrUpdateProcessCapacityService(apsProcessCapacityVo);

        return res ? Result.ok() : Result.fail();
    }

    @GetMapping("/getProcessCapacity/{page}/{size}")
    public Result getAllProcessCapacity(@PathVariable Integer page, @PathVariable Integer size) {
        ApsProcessCapacityListVo allProcessCapacity = apsProcessCapacityService.getAllProcessCapacity(page, size);
        return Result.ok(allProcessCapacity);
    }


    @PostMapping("/deleteProcessCapacity")
    public Result deleteProcessCapacity(@RequestBody List<Integer> ids){
        return apsProcessCapacityService.removeBatchByIds(ids) ? Result.ok() :Result.fail();
    }
}
