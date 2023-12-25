package com.benewake.system.controller;

import com.benewake.system.annotation.SearchHistory;
import com.benewake.system.annotation.SearchLike;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.vo.DownloadViewParams;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ResultColPageVo;
import com.benewake.system.entity.vo.baseParam.SearchLikeParam;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.MasterDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Api(tags = "主数据")
@RestController
@RequestMapping("/masterData")
public class MasterDataController {

    @Autowired
    private MasterDataService masterDataService;

//    @SearchHistory
    @ApiOperation("筛选查询")
    @PostMapping("/get/{page}/{size}")
    public Result getFiltrateDate(@PathVariable("page") Integer page, @PathVariable("size") Integer size,
                                  @RequestBody(required = false) QueryViewParams queryViewParams) {
        ResultColPageVo<Object> res = masterDataService.getFiltrateDate(page, size, queryViewParams);
        return Result.ok(res);
    }

//    @SearchLike
    @ApiOperation("自动补全")
    @PostMapping("/searchLike")
    public Result searchLike(@RequestBody SearchLikeParam searchLikeParam) {
        List<Object> res = masterDataService.searchLike(searchLikeParam);
        return Result.ok(res);
    }

    @ApiOperation("导出接口数据")
    @PostMapping("/download")
    public void downloadSchemeManagement(@RequestBody DownloadViewParams downloadParam, HttpServletResponse response) {
        if (downloadParam == null || downloadParam.getTableId() == null || downloadParam.getType() == null
                || (downloadParam.getType() == ExcelOperationEnum.CURRENT_PAGE.getCode()
                && (downloadParam.getPage() == null || downloadParam.getSize() == null))) {
            throw new BeneWakeException("数据不正确");
        }
        masterDataService.download(response, downloadParam);
    }

}
