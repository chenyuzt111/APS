package com.benewake.system.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.vo.*;
import com.benewake.system.excel.entity.ExcelFinishedProductTemplate;
import com.benewake.system.excel.entity.ExcelRawMaterialBasicDataTemplate;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsFinishedProductBasicDataService;
import com.benewake.system.service.ApsRawMaterialBasicDataService;
import com.benewake.system.service.ApsSemiFinishedBasicDataService;
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
import java.util.Objects;

@Api(tags = "物料基础数据")
@RestController
@RequestMapping("/material")
public class MaterialDataController {

    @Autowired
    private ApsRawMaterialBasicDataService rawMaterialBasicDataService;

    @Autowired
    private ApsFinishedProductBasicDataService finishedProductBasicDataService;

    @Autowired
    private ApsSemiFinishedBasicDataService semiFinishedBasicDataService;

    @ApiOperation("查询原材料基础数据 ")
    @GetMapping("/getRawMaterialBasic/{page}/{size}")
    public Result getMaterialBasicData(@RequestParam(required = false) String name, @PathVariable Integer page, @PathVariable Integer size) {
        PageResultVo<ApsRawMaterialBasicDataVo> apsRawMaterialBasicDataVo =
                rawMaterialBasicDataService.getRawMaterial(name, page ,size);
        return Result.ok(apsRawMaterialBasicDataVo);
    }


    @ApiOperation("添加或修改原材料基础数据")
    @PostMapping("/addOrUpdateRawMaterial")
    public Result addOrUpdateRawMaterial(@RequestBody ApsRawMaterialBasicDataParam param) {
        if (param == null) {
            return Result.fail("添加或修改对象不能为null");
        }
        boolean res  = rawMaterialBasicDataService.addOrUpdateRawMaterial(param);
        return res ? Result.ok() : Result.fail();
    }

    @ApiOperation("删除原材料基础数据")
    @PostMapping("/removeRawMaterial")
    public Result removeRawMaterial(@RequestBody List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Result.fail("ids不能为null");
        }
        boolean res  = rawMaterialBasicDataService.removeBatchByIds(ids);
        return res ? Result.ok() : Result.fail();
    }

    @ApiOperation("导出原材料基础数据")
    @PostMapping("/downloadRawMaterial")
    public void downloadRawMaterial(@RequestBody DownloadParam downloadParam, HttpServletResponse response) {
        if (downloadParam == null || downloadParam.getType() == null
                || (downloadParam.getType() == ExcelOperationEnum.CURRENT_PAGE.getCode()
                && (downloadParam.getPage() == null || downloadParam.getSize() == null))) {
            throw new BeneWakeException("数据不正确");
        }
        rawMaterialBasicDataService.downloadRawMaterial(response, downloadParam);
    }


    @ApiOperation("下载原材料基础数据导入模板")
    @PostMapping("/rawMaterialTemplate")
    public void rawMaterialTemplate(HttpServletResponse response) {
        try {
            ResponseUtil.setFileResp(response ,"原材料基础数据导入模板");
            EasyExcel.write(response.getOutputStream(), ExcelRawMaterialBasicDataTemplate.class).sheet("sheet1")
                    .registerWriteHandler(new SimpleColumnWidthStyleStrategy(12))
                    .doWrite((java.util.Collection<?>) null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("导入原材料基础数据")
    @PostMapping("/imporRawMaterial")
    public Result imporRawMaterial(@PathParam("type") Integer type, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("文件为空！");
        }
        String[] split = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
        if (!"xlsx".equals(split[1]) && !"xls".equals(split[1])) {
            return Result.fail("请提供.xlsx或.xls为后缀的Excel文件");
        }
        Boolean res = rawMaterialBasicDataService.saveDataByExcel(type, file);
        return res ? Result.ok() : Result.fail();
    }


    @ApiOperation("查询成品基础数据")
    @GetMapping("/getFinishedProduct/{page}/{size}")
    public Result getFinishedProduct(@RequestParam(required = false) String name, @PathVariable Integer page, @PathVariable Integer size) {
        PageResultVo<ApsFinishedProductBasicDataVo> finishedProduct =
                finishedProductBasicDataService.getFinishedProduct(name, page ,size);
        return Result.ok(finishedProduct);
    }


    @ApiOperation("添加或修改成品基础数据")
    @PostMapping("/addOrUpdateFinishedProduct")
    public Result addOrUpdateFinishedProduct(@RequestBody ApsFinishedProductBasicDataParam param) {
        if (param == null) {
            return Result.fail("添加或修改对象不能为null");
        }
        boolean res  = finishedProductBasicDataService.addOrUpdateFinishedProduct(param);
        return res ? Result.ok() : Result.fail();
    }

    @ApiOperation("删除成品基础数据")
    @PostMapping("/removeFinishedProduct")
    public Result removeFinishedProduct(@RequestBody List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Result.fail("ids不能为null");
        }
        boolean res  = finishedProductBasicDataService.removeBatchByIds(ids);
        return res ? Result.ok() : Result.fail();
    }

    @ApiOperation("导出成品基础数据")
    @PostMapping("/downloadFinishedProduct")
    public void downloadFinishedProduct(@RequestBody DownloadParam downloadParam, HttpServletResponse response) {
        if (downloadParam == null || downloadParam.getType() == null
                || (downloadParam.getType() == ExcelOperationEnum.CURRENT_PAGE.getCode()
                && (downloadParam.getPage() == null || downloadParam.getSize() == null))) {
            throw new BeneWakeException("数据不正确");
        }
        finishedProductBasicDataService.downloadFinishedProduct(response, downloadParam);
    }


    @ApiOperation("下载成品基础数据导入模板")
    @PostMapping("/finishedProductTemplate")
    public void finishedProductTemplate(HttpServletResponse response) {
        try {
            ResponseUtil.setFileResp(response ,"成品基础数据导入模板");
            EasyExcel.write(response.getOutputStream(), ExcelFinishedProductTemplate.class).sheet("sheet1")
                    .registerWriteHandler(new SimpleColumnWidthStyleStrategy(12))
                    .doWrite((java.util.Collection<?>) null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("导入成品基础数据")
    @PostMapping("/imporFinishedProduct")
    public Result imporFinishedProduct(@PathParam("type") Integer type, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("文件为空！");
        }
        String[] split = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
        if (!"xlsx".equals(split[1]) && !"xls".equals(split[1])) {
            return Result.fail("请提供.xlsx或.xls为后缀的Excel文件");
        }
        Boolean res = finishedProductBasicDataService.saveDataByExcel(type, file);
        return res ? Result.ok() : Result.fail();
    }

//    semiFinishedBasicDataService
    @ApiOperation("查询半成品基础数据")
    @GetMapping("/getSemiFinished/{page}/{size}")
    public Result getSemiFinished(@RequestParam(required = false) String name, @PathVariable Integer page, @PathVariable Integer size) {
        PageResultVo<ApsSemiFinishedBasicDataVo> semiFinished =
                semiFinishedBasicDataService.getSemiFinished(name, page ,size);
        return Result.ok(semiFinished);
    }


    @ApiOperation("添加或修改半成品基础数据")
    @PostMapping("/addOrUpdateSemiFinished")
    public Result addOrUpdateSemiFinished(@RequestBody ApsSemiFinishedBasicDataParam param) {
        if (param == null) {
            return Result.fail("添加或修改对象不能为null");
        }
        boolean res  = semiFinishedBasicDataService.addOrUpdateSemiFinished(param);
        return res ? Result.ok() : Result.fail();
    }

    @ApiOperation("删除半成品基础数据")
    @PostMapping("/removeSemiFinished")
    public Result removeSemiFinished(@RequestBody List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Result.fail("ids不能为null");
        }
        boolean res  = semiFinishedBasicDataService.removeBatchByIds(ids);
        return res ? Result.ok() : Result.fail();
    }

    @ApiOperation("导出半成品基础数据")
    @PostMapping("/downloadSemiFinished")
    public void downloadSemiFinished(@RequestBody DownloadParam downloadParam, HttpServletResponse response) {
        if (downloadParam == null || downloadParam.getType() == null
                || (downloadParam.getType() == ExcelOperationEnum.CURRENT_PAGE.getCode()
                && (downloadParam.getPage() == null || downloadParam.getSize() == null))) {
            throw new BeneWakeException("数据不正确");
        }
        semiFinishedBasicDataService.downloadSemiFinished(response, downloadParam);
    }


//    @ApiOperation("下载半成品基础数据导入模板")
//    @PostMapping("/finishedProductTemplate")
//    public void finishedProductTemplate(HttpServletResponse response) {
//        try {
//            ResponseUtil.setFileResp(response ,"半成品基础数据导入模板");
//            EasyExcel.write(response.getOutputStream(), ExcelFinishedProductTemplate.class).sheet("sheet1")
//                    .registerWriteHandler(new SimpleColumnWidthStyleStrategy(12))
//                    .doWrite((java.util.Collection<?>) null);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @ApiOperation("导入半成品基础数据")
//    @PostMapping("/imporFinishedProduct")
//    public Result imporFinishedProduct(@PathParam("type") Integer type, @RequestParam("file") MultipartFile file) {
//        if (file.isEmpty()) {
//            return Result.fail("文件为空！");
//        }
//        String[] split = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
//        if (!"xlsx".equals(split[1]) && !"xls".equals(split[1])) {
//            return Result.fail("请提供.xlsx或.xls为后缀的Excel文件");
//        }
//        Boolean res = finishedProductBasicDataService.saveDataByExcel(type, file);
//        return res ? Result.ok() : Result.fail();
//    }
}
