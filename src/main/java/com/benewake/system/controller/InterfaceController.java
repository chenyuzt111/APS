package com.benewake.system.controller;

import com.alibaba.fastjson2.JSON;
import com.benewake.system.entity.ApsImmediatelyInventory;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.enums.InterfaceDataType;
import com.benewake.system.entity.vo.PageListRestVo;
import com.benewake.system.service.ApsDailyDataUploadService;
import com.benewake.system.service.InterfaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Map;

import static org.python.bouncycastle.asn1.x500.style.RFC4519Style.o;

@Api(tags = "接口数据")
@RestController
@RequestMapping("/interface")
public class InterfaceController {


    @Autowired
    private InterfaceService interfaceService;

    @Autowired
    private ApsDailyDataUploadService dailyDataUploadService;


    @ApiOperation("查询")
    @PostMapping("/getMultipleVersionsData")
    public Result getMultipleVersionsData(@PathParam("page") Integer page, @PathParam("size") Integer size, @PathParam("type") Integer type) {
        List<Object> apsResult = interfaceService.getMultipleVersionsData(page, size, type);
        return Result.ok(apsResult);
    }

    @ApiOperation("查询")
    @GetMapping("/getAllPage/{page}/{size}")
    public Result getAllPage(@PathVariable("page") Integer page, @PathVariable("size") Integer size, @PathParam("type") Integer type) {
        PageListRestVo<Object> apsResult = interfaceService.getAllPage(page, size, type);
        return Result.ok(apsResult);
    }

    @ApiOperation("新增")
    @PostMapping("/add")
    public Result add(@RequestBody String request, @PathParam("type") Integer type) {
        Boolean res = interfaceService.add(request, type);
        return res ? Result.ok() : Result.fail();
    }

    @ApiOperation("修改")
    @PostMapping("/update")
    public Result update(@RequestBody String request, @PathParam("type") Integer type) {
        Boolean res = interfaceService.update(request, type);
        return res ? Result.ok() : Result.fail();
    }

    @ApiOperation("删除")
    @PostMapping("/delete")
    public Result delete(@RequestBody List<Integer> ids, @PathParam("type") Integer type) {
        if (CollectionUtils.isEmpty(ids)) {
            return Result.fail("id不能为null");
        }
        Boolean res = interfaceService.delete(ids, type);
        return res ? Result.ok() : Result.fail();
    }

    @PostMapping("/importExcel")
    public Result addOrdersByExcel(@PathParam("type") Integer type ,@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("文件为空！");
        }
        String[] split = file.getOriginalFilename().split("\\.");
        if (!"xlsx".equals(split[1]) && !"xls".equals(split[1])) {
            return Result.fail("请提供.xlsx或.xls为后缀的Excel文件");
        }
        Boolean res = dailyDataUploadService.saveDataByExcel(file);
        return res ? Result.ok() : Result.fail();
    }
}
