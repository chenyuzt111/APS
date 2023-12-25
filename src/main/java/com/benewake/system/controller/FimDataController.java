package com.benewake.system.controller;

import com.alibaba.excel.EasyExcel;
import com.benewake.system.annotation.SearchHistory;
import com.benewake.system.annotation.SearchLike;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.vo.*;
import com.benewake.system.entity.vo.baseParam.SearchLikeParam;
import com.benewake.system.excel.entity.ExcelFimRequestTemplate;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsFimRequestService;
import com.benewake.system.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.List;

@Slf4j
@Api(tags = "接口数据")
@RestController
@RequestMapping("/fimData")
public class FimDataController {

    @Autowired
    private ApsFimRequestService fimRequestService;

//    @ApiOperation("查询fim需求")
//    @GetMapping("/getFimRequestPage/{page}/{size}")
//    public Result getFimRequestPage(@PathVariable Integer page, @PathVariable Integer size) {
//        PageResultVo<ApsFimRequestVo> pageResultVo = fimRequestService.getFimRequestPage(page, size);
//        return Result.ok(pageResultVo);
//    }

//    @SearchHistory
//    @ApiOperation("查询fim需求")
//    @PostMapping("/getFimRequestPageFilter/{page}/{size}")
//    public Result getFimRequestPageFilter(@PathVariable Integer page, @PathVariable Integer size,
//                                          @RequestBody(required = false) QueryViewParams queryViewParams) {
//        ResultColPageVo<ApsFimRequestVo> pageResultVo = fimRequestService.getFimRequestPageFilter(page, size, queryViewParams);
//        return Result.ok(pageResultVo);
//    }

//    @SearchLike
//    @ApiOperation("fim需求自动补全")
//    @PostMapping("/fimRequestSearchLike")
//    public Result fimRequestSearchLike(@RequestBody SearchLikeParam searchLikeParam) {
//        List<Object> res = fimRequestService.searchLike(searchLikeParam);
//        return Result.ok(res);
//    }

    @ApiOperation("添加或修改fim需求")
    @PostMapping("/addOrUpdateFimRequest")
    public Result addOrUpdateFimRequest(@RequestBody ApsFimRequestParam fimRequestParam) {
        if (fimRequestParam == null) {
            return Result.fail("不能为null");
        }
        Boolean res = fimRequestService.addOrUpdateFimRequest(fimRequestParam);
        return res ? Result.ok() : Result.fail();
    }

    @ApiOperation("删除fim需求")
    @PostMapping("/removeFimRequest")
    public Result removeFimRequest(@RequestBody List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Result.fail("不能为null");
        }
        Boolean res = fimRequestService.removeBatchByIds(ids);
        return res ? Result.ok() : Result.fail();
    }

//    @ApiOperation("导出排程结果数据")
//    @PostMapping("/download")
//    public void download(@RequestBody DownloadViewParams downloadParam, HttpServletResponse response) {
//        if (downloadParam == null || downloadParam.getTableId() == null
//                || (downloadParam.getType() == ExcelOperationEnum.CURRENT_PAGE.getCode()
//                && (downloadParam.getPage() == null || downloadParam.getSize() == null))) {
//            throw new BeneWakeException("数据不正确");
//        }
//        fimRequestService.downloadFimRequest(response, downloadParam);
//    }

    @ApiOperation("下载fim需求导入模板")
    @PostMapping("/fimRequestTemplate")
    public void fimRequestTemplate(HttpServletResponse response) {
        try {
            ResponseUtil.setFileResp(response, "fim需求导入模板");
            EasyExcel.write(response.getOutputStream(), ExcelFimRequestTemplate.class).sheet("sheet1")
                    .doWrite((java.util.Collection<?>) null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("导入fim需求")
    @PostMapping("/importFimRequest")
    public Result importFimRequest(@PathParam("type") Integer type, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("文件为空！");
        }
        String[] split = file.getOriginalFilename().split("\\.");
        if (!"xlsx".equals(split[1]) && !"xls".equals(split[1])) {
            return Result.fail("请提供.xlsx或.xls为后缀的Excel文件");
        }
        Boolean res = fimRequestService.saveDataByExcel(type, file);
        return res ? Result.ok() : Result.fail();
    }
}