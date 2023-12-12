package com.benewake.system.service.scheduling.result;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.*;
import com.benewake.system.entity.dto.ApsProductionPlanDto;
import com.benewake.system.entity.enums.SchedulingResultType;
import com.benewake.system.entity.enums.TableVersionState;
import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ResultColPageVo;
import com.benewake.system.entity.vo.ViewColParam;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsColumnTableMapper;
import com.benewake.system.mapper.ApsViewColTableMapper;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.query.QueryStrategyFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ApsSchedulingResuleBase {

    QueryStrategyFactory queryStrategyFactory = new QueryStrategyFactory();

    default Integer getApsTableVersion(Integer code, ApsTableVersionService apsTableVersionService) {
        LambdaQueryWrapper<ApsTableVersion> apsTableVersionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsTableVersionLambdaQueryWrapper.eq(ApsTableVersion::getTableId, code)
                .eq(ApsTableVersion::getState, TableVersionState.SUCCESS.getCode())
                .orderByDesc(ApsTableVersion::getVersionNumber)
                .last("limit 1");

        ApsTableVersion one = apsTableVersionService.getOne(apsTableVersionLambdaQueryWrapper);
        if (one == null || one.getTableVersion() == null) {
            throw new BeneWakeException("还未有排程数据");
        }
        return one.getTableVersion();
    }

    default Integer getMaxVersionAndSave() {
        Integer curVersion = null;
        try {
            QueryWrapper<Object> objectQueryWrapper = new QueryWrapper<>();
            objectQueryWrapper.orderByDesc("version")
                    .last("limit 1")
                    .select("version");

            IService iService = (IService) this;
            Object iServiceOne = iService.getOne(objectQueryWrapper);
            curVersion = 0;
            if (iServiceOne != null) {
                //拿出最大版本
                Class<?> aClass = iServiceOne.getClass();
                Field version = aClass.getDeclaredField("version");
                version.setAccessible(true);
                Integer o = (Integer) version.get(iServiceOne);
                if (o == null) {
                    curVersion = 1;
                } else {
                    curVersion = o + 1;
                }
                version.setAccessible(false);
                UpdateWrapper<Object> objectUpdateWrapper = new UpdateWrapper<>();
                objectUpdateWrapper.isNull("version").set("version", curVersion);
                iService.update(objectUpdateWrapper);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return curVersion;
    }

    default ApsTableVersionService getTableVersionService() {
        throw new BeneWakeException("该表不需要使用该方法");
    }

    default ApsViewColTableMapper getViewColTableMapper() {
        throw new BeneWakeException("该表不需要使用该方法");
    }

    default ApsColumnTableMapper getColumnTableMapper() {
        throw new BeneWakeException("该表不需要使用该方法");
    }

    default ResultColPageVo<Object> commonFiltrate(Integer page, Integer size, SchedulingResultType schedulingResultType, QueryViewParams queryViewParams,
                                                   BiFunction<Page<Object>, QueryWrapper<Object>, Page<Object>> function) {

        Long viewId = queryViewParams != null ? queryViewParams.getViewId() : null;
        List<ViewColParam> cols = queryViewParams != null ? queryViewParams.getCols() : null;
        Integer apsTableVersion = getApsTableVersion(schedulingResultType.getCode(), getTableVersionService());
        // 构造查询条件 根据列后面的值 构造queryWrapper
        // 如果列是null 那么 就有可能 是视图或者 全部页
        if (CollectionUtils.isEmpty(cols)) {
            List<ColumnVo> columnVos = Collections.emptyList();
            QueryWrapper<Object> queryWrapper = new QueryWrapper<>().eq("version", apsTableVersion);
            // 如果是视图拼装视图自带的筛选条件
            if (viewId != null) {
                List<ViewColumnDto> viewColTables = getViewColTableMapper().getViewColByViewId(viewId, null);
                queryWrapper = buildQueryWrapper(viewColTables, apsTableVersion);
                // 构造返回给前端的列
                columnVos = buildColumnVos(viewColTables);
            }
            // 构造分页查询条件
            Page<Object> apsProductionPlanPage = buildPage(page, size);
            Page<Object> planPage = function.apply(apsProductionPlanPage, queryWrapper);
            return buildResultColPageVo(columnVos, planPage);
        } else {
            // 如果列不是null 那么他有可能是视图 或者全部页 不管是视图还是全部页 都去拼sql
            List<Integer> colIds = cols.stream().map(ViewColParam::getColId).collect(Collectors.toList());
            List<ApsColumnTable> columnTables = getColumnTableMapper().selectBatchIds(colIds);
            Map<Integer, ViewColParam> colIdMap = cols.stream()
                    .collect(Collectors.toMap(ViewColParam::getColId, x -> x, (existing, replacement) -> existing));
            Map<Integer, ApsColumnTable> colIdToEnName = columnTables.stream()
                    .collect(Collectors.toMap(ApsColumnTable::getId, x -> x, (existing, replacement) -> existing));
            QueryWrapper<Object> queryWrapper = buildQueryWrapper(colIdMap, colIdToEnName, apsTableVersion);
            // 构造分页查询条件
            Page<Object> apsProductionPlanPage = buildPage(page, size);
            Page<Object> planPage = function.apply(apsProductionPlanPage, queryWrapper);
            // 如果是点击查询 就不需要回填搜素框
            return buildResultColPageVo(Collections.emptyList(), planPage);
        }
    }

    default QueryWrapper<Object> buildQueryWrapper(List<ViewColumnDto> viewColTables, Integer apsTableVersion) {
        QueryWrapper<Object> queryWrapper = new QueryWrapper<>().eq("version", apsTableVersion);

        for (ViewColumnDto viewColTable : viewColTables) {
            String valueOperator = viewColTable.getValueOperator();
            String enColName = viewColTable.getEnColName();
            String colValue = viewColTable.getColValue();

            if (StringUtils.isNotEmpty(valueOperator)) {
                queryStrategyFactory.getStrategy(valueOperator)
                        .apply(queryWrapper, enColName, colValue);
            }
        }

        return queryWrapper;
    }

    default List<ColumnVo> buildColumnVos(List<ViewColumnDto> viewColTables) {
        return viewColTables.stream().map(x -> {
            ColumnVo columnVo = new ColumnVo();
            columnVo.setId(Math.toIntExact(x.getId()));
            columnVo.setColId(Math.toIntExact(x.getColId()));
            columnVo.setChColName(x.getChColName());
            columnVo.setVoColName(x.getVoColName());
            columnVo.setValueOperator(x.getValueOperator());
            columnVo.setColValue(x.getColValue());
            columnVo.setColSeq(x.getColSeq());
            return columnVo;
        }).collect(Collectors.toList());
    }

    default QueryWrapper<Object> buildQueryWrapper(Map<Integer, ViewColParam> colIdMap, Map<Integer, ApsColumnTable> colIdToEnName, Integer apsTableVersion) {
        QueryWrapper<Object> queryWrapper = new QueryWrapper<>().eq("version", apsTableVersion);
        for (Map.Entry<Integer, ViewColParam> colParamEntry : colIdMap.entrySet()) {
            ViewColParam colParam = colParamEntry.getValue();
            Integer colId = colParam.getColId();
            ApsColumnTable apsColumnTable = colIdToEnName.get(colId);
            String enColName = apsColumnTable.getEnColName();
            String valueOperator = colParam.getValueOperator();
            String colValue = colParam.getColValue();
            if (StringUtils.isNotEmpty(valueOperator)) {
                queryStrategyFactory.getStrategy(valueOperator)
                        .apply(queryWrapper, enColName, colValue);
            }
        }
        return queryWrapper;
    }

    default Page<Object> buildPage(Integer page, Integer size) {
        Page<Object> apsProductionPlanPage = new Page<>();
        apsProductionPlanPage.setSize(size);
        apsProductionPlanPage.setCurrent(page);
        return apsProductionPlanPage;
    }

    default ResultColPageVo<Object> buildResultColPageVo(List<ColumnVo> columnVos, Page<Object> planPage) {
        ResultColPageVo<Object> productionPlanDtoResultColPageVo = new ResultColPageVo<>();
        productionPlanDtoResultColPageVo.setList(planPage.getRecords());
        productionPlanDtoResultColPageVo.setColumnTables(columnVos);
        productionPlanDtoResultColPageVo.setPage(Math.toIntExact(planPage.getCurrent()));
        productionPlanDtoResultColPageVo.setSize(Math.toIntExact(planPage.getSize()));
        productionPlanDtoResultColPageVo.setTotal(planPage.getTotal());
        productionPlanDtoResultColPageVo.setPages(planPage.getPages());
        return productionPlanDtoResultColPageVo;
    }

}
