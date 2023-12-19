package com.benewake.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.ApsColumnTable;
import com.benewake.system.entity.ApsViewTable;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.enums.SchedulingResultType;
import com.benewake.system.entity.vo.DownloadViewParams;
import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ResultColPageVo;
import com.benewake.system.entity.vo.baseParam.SearchLikeParam;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsColumnTableService;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.ApsViewTableService;
import com.benewake.system.service.SchedulingResultService;
import com.benewake.system.service.scheduling.result.ApsSchedulingResuleBase;
import com.benewake.system.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.benewake.system.utils.BenewakeStringUtils.removeAs;

@Slf4j
@Component
public class SchedulingResultServiceImpl implements SchedulingResultService {

    @Autowired
    private Map<String, ApsSchedulingResuleBase> schedulingResuleBaseMap;

    @Autowired
    private ApsColumnTableService columnTableService;

    @Autowired
    private ApsViewTableService viewTableService;

    public List<Object> searchLike(SearchLikeParam searchLikeParam) {
        SchedulingResultType schedulingResultType = SchedulingResultType.valueOfCode(searchLikeParam.getTableId());
        if (schedulingResultType == null) {
            throw new BeneWakeException("当前tableId不正确");
        }
        ApsColumnTable columnTable = columnTableService.getById(searchLikeParam.getColId());
        if (columnTable == null) {
            throw new BeneWakeException("当前colId不正确");
        }
        ApsSchedulingResuleBase apsSchedulingResuleBase = schedulingResuleBaseMap.get(schedulingResultType.getServiceName());
        String voColName = columnTable.getVoColName();
        QueryWrapper<Object> queryWrapper = buildQueryWrapper(searchLikeParam, columnTable);
        List<Object> searchLikeObj = apsSchedulingResuleBase.searchLike(queryWrapper);
        if (CollectionUtils.isEmpty(searchLikeObj)) {
            return Collections.emptyList();
        }
        return searchLikeObj.stream().map(x -> {
            Object searchLike;
            try {
                Field declaredField = x.getClass().getDeclaredField(voColName);
                declaredField.setAccessible(true);
                searchLike = declaredField.get(x);
                declaredField.setAccessible(false);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error("反射发生异常{}", e.getMessage());
                throw new BeneWakeException("服务器资源问题");
            }
            return searchLike;
        }).sorted().collect(Collectors.toList());
    }

    @Override
    public void download(HttpServletResponse response, DownloadViewParams downloadParam) {
        try {
            SchedulingResultType schedulingResultType = SchedulingResultType.valueOfCode(downloadParam.getTableId());
            if (schedulingResultType == null) {
                throw new BeneWakeException("当前tableId不正确");
            }
            ApsSchedulingResuleBase apsSchedulingResuleBase = schedulingResuleBaseMap.get(schedulingResultType.getServiceName());
            QueryViewParams queryViewParams = buildQueryViewParams(downloadParam);
            ResultColPageVo<Object> resultFiltrate;
            if (downloadParam.getType() == ExcelOperationEnum.ALL_PAGES.getCode()) {
                Integer size = Math.toIntExact(((IService) apsSchedulingResuleBase).count());
                resultFiltrate = apsSchedulingResuleBase
                        .getResultFiltrate(1, size, queryViewParams);
            } else {
                resultFiltrate = apsSchedulingResuleBase
                        .getResultFiltrate(downloadParam.getPage(), downloadParam.getSize(), queryViewParams);
            }
            List<String> colNames = null;
            if (downloadParam.getViewId() != null) {
                colNames = viewTableService.selectColNameByViewId(downloadParam.getViewId());
            }
            List<Object> resList = resultFiltrate.getList();
            ResponseUtil.setFileResp(response, "排程结果导出");
            if (CollectionUtils.isNotEmpty(resList)) {
                EasyExcel.write(response.getOutputStream(), resList.get(0).getClass())
                        .sheet("sheet1")
                        .includeColumnFieldNames(colNames)
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                        .doWrite(resList);
            } else {
                throw new BeneWakeException("当前数据为空");
            }
        } catch (Exception e) {
            if (!(e instanceof BeneWakeException)) {
                log.error("排程结果导出失败" + e.getMessage());
                throw new BeneWakeException("排程结果导出失败");
            }
            log.info("排程结果数据为空" + e.getMessage());
            throw new BeneWakeException(e.getMessage());
        }
    }


    private QueryViewParams buildQueryViewParams(DownloadViewParams downloadParam) {
        QueryViewParams queryViewParams = new QueryViewParams();
        queryViewParams.setTableId(Math.toIntExact(downloadParam.getViewId()));
        queryViewParams.setViewId(downloadParam.getViewId());
        queryViewParams.setCols(downloadParam.getCols());
        return queryViewParams;
    }

    private QueryWrapper<Object> buildQueryWrapper(SearchLikeParam searchLikeParam, ApsColumnTable columnTable) {
        QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
        String enColName = columnTable.getEnColName();
        queryWrapper
                .like(removeAs(enColName), searchLikeParam.getValue())
                .select(enColName);
        return queryWrapper;
    }
}
