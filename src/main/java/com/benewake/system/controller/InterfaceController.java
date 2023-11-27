package com.benewake.system.controller;

import com.alibaba.excel.EasyExcel;
import com.benewake.system.entity.ApsDailyDataUpload;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.dto.ApsDailyDataUploadDto;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.vo.ApsDailyDataUploadParam;
import com.benewake.system.entity.vo.DownloadParam;
import com.benewake.system.entity.vo.PageListRestVo;
import com.benewake.system.excel.entity.ExcelDailyDataUploadTemplate;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsDailyDataUploadService;
import com.benewake.system.service.InterfaceService;
import com.benewake.system.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Api(tags = "接口数据")
@RestController
@RequestMapping("/interface")
public class InterfaceController {


    @Autowired
    private InterfaceService interfaceService;

    @Autowired
    private ApsDailyDataUploadService dailyDataUploadService;


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

    @ApiOperation("导出接口数据")
    @PostMapping("/downloadInterfaceDate")
    public void downloadSchemeManagement(@RequestBody DownloadParam downloadParam, @PathParam("type") Integer type, HttpServletResponse response) {
        if (downloadParam == null || downloadParam.getType() == null
                || (downloadParam.getType() == ExcelOperationEnum.CURRENT_PAGE.getCode()
                && (downloadParam.getPage() == null || downloadParam.getSize() == null))) {
            throw new BeneWakeException("数据不正确");
        }
        interfaceService.downloadProcessCapacity(response, type, downloadParam);
    }


    @ApiOperation("下载导入模板")
    @PostMapping("/downloadInterfaceTemplate")
    public void downloadInterfaceTemplate(@PathParam("type") Integer type, HttpServletResponse response) {
        interfaceService.downloadInterfaceTemplate(type ,response);
    }

    @ApiOperation("导入接口数据")
    @PostMapping("/importInterfaceData")
    public Result importInterfaceData(@PathParam("code") Integer code , @PathParam("type") Integer type,
                                      @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("文件为空！");
        }
        String[] split = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
        if (!"xlsx".equals(split[1]) && !"xls".equals(split[1])) {
            return Result.fail("请提供.xlsx或.xls为后缀的Excel文件");
        }

        Boolean res = interfaceService.importInterfaceData(code ,type, file);
        return res ? Result.ok() : Result.fail();
    }



    //    ---------------日别数据--------------------
    @ApiOperation("获取日别数据")
    @GetMapping("/getDailyDataList/{page}/{size}")
    public Result getDailyDataList(@PathVariable Integer page, @PathVariable Integer size) {
        PageListRestVo<ApsDailyDataUploadDto> pageListRestVo = dailyDataUploadService.getDailyDataListPage(page, size);
        return Result.ok(pageListRestVo);
    }

    @ApiOperation("增加或修改日别数据")
    @PostMapping("/addOrUpdateDailyData")
    public Result addOrUpdateDailyData(@RequestBody ApsDailyDataUploadParam dailyDataUploadParam) {
        if (dailyDataUploadParam == null) {
            return Result.fail("数据不能为null");
        }
        Boolean res = dailyDataUploadService.addOrUpdateDailyData(dailyDataUploadParam);
        return res ? Result.ok() : Result.fail();
    }


    @ApiOperation("删除日别数据")
    @PostMapping("/removeDailyData")
    public Result removeDailyData(@RequestBody List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Result.fail("id不能为null");
        }
        Boolean res = dailyDataUploadService.removeBatchByIds(ids);
        return res ? Result.ok() : Result.fail();
    }

    @ApiOperation("导出日别数据")
    @PostMapping("/downloadDailyData")
    public void downloadDailyData(@RequestBody DownloadParam downloadParam, HttpServletResponse response) {
        if (downloadParam == null || downloadParam.getType() == null
                || (downloadParam.getType() == ExcelOperationEnum.CURRENT_PAGE.getCode()
                && (downloadParam.getPage() == null || downloadParam.getSize() == null))) {
            throw new BeneWakeException("数据不正确");
        }
        dailyDataUploadService.downloadDailyData(response, downloadParam);
    }

    @ApiOperation("导入日别数据")
    @PostMapping("/importloadDailyData")
    public Result importloadDailyData(@PathParam("type") Integer type, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("文件为空！");
        }
        String[] split = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
        if (!"xlsx".equals(split[1]) && !"xls".equals(split[1])) {
            return Result.fail("请提供.xlsx或.xls为后缀的Excel文件");
        }

        Boolean res = dailyDataUploadService.importloadDailyData(type, file);
        return res ? Result.ok() : Result.fail();
    }

    @ApiOperation("下载导入日别数据模板")
    @PostMapping("/downloadDailyDataUploadTemplate")
    public void downloadDailyDataUploadTemplate(HttpServletResponse response) {
        try {
            ResponseUtil.setFileResp(response , "日别数据模板");
            EasyExcel.write(response.getOutputStream(), ExcelDailyDataUploadTemplate.class)
                    .sheet("sheet1").doWrite((Collection<?>) null);
        } catch (Exception e) {
            log.info("下载导入日别数据模板失败" + LocalDateTime.now());
            throw new BeneWakeException("下载导入日别数据模板失败");
        }
    }
}
