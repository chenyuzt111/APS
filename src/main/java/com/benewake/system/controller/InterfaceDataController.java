package com.benewake.system.controller;

import com.benewake.system.entity.Result;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.InterfaceDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    InterfaceDataService interfaceDataService;

    @ApiOperation("刷新接口数据")
    @PostMapping("update")
    public Result update(@RequestBody List<Integer> ids) throws Exception {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BeneWakeException("ids不能为空");
        }
        Boolean UpdateResult = interfaceDataService.updateData(ids);
        if (Boolean.TRUE.equals(UpdateResult))
            return Result.ok();
        else
            return Result.fail();
    }
}
