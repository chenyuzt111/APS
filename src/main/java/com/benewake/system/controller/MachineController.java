package com.benewake.system.controller;


import com.alibaba.excel.EasyExcel;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.vo.ApsProductFamilyMachineTablePageVo;
import com.benewake.system.entity.vo.ApsProductFamilyMachineTableParam;
import com.benewake.system.entity.vo.DownloadParam;
import com.benewake.system.excel.entity.ExcelMachineTableTemplate;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsProductFamilyMachineTableService;
import com.benewake.system.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.List;

@Api(tags = "机器管理")
@RestController
@RequestMapping("/machine")
public class MachineController {

    @Autowired
    private ApsProductFamilyMachineTableService apsProductFamilyMachineTableService;


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


    @ApiOperation("下载机器管理列表导入模板")
    @PostMapping("/downloadMachineTableTemplate")
    public void downloadMachineTableTemplate(HttpServletResponse response) {
        try {
            ResponseUtil.setFileResp(response ,"机器管理列表导入模板");
            EasyExcel.write(response.getOutputStream(), ExcelMachineTableTemplate.class).sheet("sheet1")
                    .doWrite((java.util.Collection<?>) null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @ApiOperation("导入机器管理列表")
    @PostMapping("/importMachineTable")
    public Result importMachineTable(@PathParam("type") Integer type, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("文件为空！");
        }
        String[] split = file.getOriginalFilename().split("\\.");
        if (!"xlsx".equals(split[1]) && !"xls".equals(split[1])) {
            return Result.fail("请提供.xlsx或.xls为后缀的Excel文件");
        }
        Boolean res = apsProductFamilyMachineTableService.saveDataByExcel(type, file);
        return res ? Result.ok() : Result.fail();
    }



}
