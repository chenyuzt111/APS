package com.benewake.system.service.scheduling.result;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.*;
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
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.benewake.system.utils.BenewakeStringUtils.removeAs;
import static org.apache.commons.lang3.StringUtils.indexOf;

public interface ApsSchedulingResuleBase {

    QueryStrategyFactory queryStrategyFactory = new QueryStrategyFactory();

    default ResultColPageVo<Object> getResultFiltrate(Integer page, Integer size, QueryViewParams queryViewParams) {
        throw new BeneWakeException("当前表不能使用该查询");
    }

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
        if (cols == null) {
            List<ColumnVo> columnVos = Collections.emptyList();
            List<SortVo> sortVos = Collections.emptyList();
            QueryWrapper<Object> queryWrapper = new QueryWrapper<>().eq("version", apsTableVersion);
            // 如果是视图拼装视图自带的筛选条件
            if (viewId != null) {
                List<ViewColumnDto> viewColTables = getViewColTableMapper().getViewColByViewId(viewId, null);
                queryWrapper = buildQueryWrapper(viewColTables, apsTableVersion);
                // 构造返回给前端的列
                columnVos = buildColumnVos(viewColTables);
                sortVos = buildSortVos(viewColTables);
            }
            // 构造分页查询条件
            Page<Object> apsProductionPlanPage = buildPage(page, size);
            Page<Object> planPage = function.apply(apsProductionPlanPage, queryWrapper);
            return buildResultColPageVo(columnVos, sortVos, planPage);
        } else {
            // 如果列不是null 那么他有可能是视图 或者全部页 不管是视图还是全部页 都去拼sql
            List<Integer> colIds = cols.stream().map(ViewColParam::getColId).filter(Objects::nonNull).collect(Collectors.toList());
            List<ApsColumnTable> columnTables;
            QueryWrapper<Object> queryWrapper = new QueryWrapper<>().eq("version", apsTableVersion);
            if (CollectionUtils.isNotEmpty(colIds)) {
                columnTables = getColumnTableMapper().selectBatchIds(colIds);
                Map<Integer, List<ViewColParam>> colIdMap = cols.stream()
                        .filter(viewColParam -> viewColParam.getColId() != null)
                        .collect(Collectors.groupingBy(ViewColParam::getColId));
                Map<Integer, ApsColumnTable> colIdToEnName = columnTables.stream()
                        .collect(Collectors.toMap(ApsColumnTable::getId, x -> x, (existing, replacement) -> existing));
                queryWrapper = buildQueryWrapper(colIdMap, colIdToEnName, apsTableVersion);
            }

            // 构造分页查询条件
            Page<Object> apsProductionPlanPage = buildPage(page, size);
            Page<Object> planPage = function.apply(apsProductionPlanPage, queryWrapper);
            // 如果是点击查询 就不需要回填搜素框
            return buildResultColPageVo(Collections.emptyList(), Collections.emptyList(), planPage);
        }
    }

    default List<SortVo> buildSortVos(List<ViewColumnDto> viewColTables) {
        return viewColTables.stream().filter(x -> {
            String valueOperator = x.getValueOperator();
            return StringUtils.isNotEmpty(valueOperator) &&
                    (valueOperator.equals("ascending") || (valueOperator.equals("descending")));
        }).map(x -> {
                    SortVo sortVo = new SortVo();
                    sortVo.setId(Math.toIntExact(x.getId()));
                    sortVo.setColId(Math.toIntExact(x.getColId()));
                    sortVo.setChColName(x.getChColName());
                    sortVo.setVoColName(x.getVoColName());
                    sortVo.setValueOperator(x.getValueOperator());
                    sortVo.setColValue(x.getColValue());
                    sortVo.setColSeq(x.getColSeq());
                    return sortVo;
                }
        ).collect(Collectors.toList());
    }

    default QueryWrapper<Object> buildQueryWrapper(List<ViewColumnDto> viewColTables, Integer apsTableVersion) {
        QueryWrapper<Object> queryWrapper = new QueryWrapper<>().eq("version", apsTableVersion);
        StringBuilder selectCol = new StringBuilder();
        for (ViewColumnDto viewColTable : viewColTables) {
            String valueOperator = viewColTable.getValueOperator();
            String enColName = viewColTable.getEnColName();
            String colValue = viewColTable.getColValue();
            selectCol.append(",").append(enColName);
            if (StringUtils.isNotEmpty(valueOperator) && StringUtils.isNotEmpty(enColName)) {
                queryStrategyFactory.getStrategy(valueOperator)
                        .apply(queryWrapper, removeAs(enColName), colValue);
            }
        }

        queryWrapper.select(selectCol.toString());
        return queryWrapper;
    }

    default List<ColumnVo> buildColumnVos(List<ViewColumnDto> viewColTables) {
        return viewColTables.stream().map(x -> {
            ColumnVo columnVo = new ColumnVo();
            columnVo.setId(Math.toIntExact(x.getId()));
            columnVo.setColId(Math.toIntExact(x.getColId()));
            columnVo.setChColName(x.getChColName());
            columnVo.setVoColName(x.getVoColName());
            String valueOperator = x.getValueOperator();
            if (valueOperator != null && (valueOperator.equals("ascending") || valueOperator.equals("descending"))) {
                columnVo.setValueOperator(null);
                columnVo.setColValue(null);
            } else {
                columnVo.setValueOperator(x.getValueOperator());
                columnVo.setColValue(x.getColValue());
            }
            columnVo.setColSeq(x.getColSeq());
            return columnVo;
        }).collect(Collectors.toList());
    }

    default QueryWrapper<Object> buildQueryWrapper(Map<Integer, List<ViewColParam>> colIdMap, Map<Integer, ApsColumnTable> colIdToEnName, Integer apsTableVersion) {
        QueryWrapper<Object> queryWrapper = new QueryWrapper<>().eq("version", apsTableVersion);
        colIdMap.forEach((colId, viewColParams) ->
                viewColParams.forEach(viewColParam -> {
                    ApsColumnTable apsColumnTable = colIdToEnName.get(colId);
                    String enColName = apsColumnTable.getEnColName();
                    String valueOperator = viewColParam.getValueOperator();
                    String colValue = viewColParam.getColValue();

                    if (StringUtils.isNotEmpty(valueOperator)) {
                        queryStrategyFactory.getStrategy(valueOperator)
                                .apply(queryWrapper, removeAs(enColName), colValue);
                    }
                }));
        return queryWrapper;
    }

    default Page<Object> buildPage(Integer page, Integer size) {
        Page<Object> apsProductionPlanPage = new Page<>();
        apsProductionPlanPage.setSize(size);
        apsProductionPlanPage.setCurrent(page);
        return apsProductionPlanPage;
    }

    default ResultColPageVo<Object> buildResultColPageVo(List<ColumnVo> columnVos, List<SortVo> sortVos, Page<Object> planPage) {
        ResultColPageVo<Object> productionPlanDtoResultColPageVo = new ResultColPageVo<>();
        productionPlanDtoResultColPageVo.setList(planPage.getRecords());
        productionPlanDtoResultColPageVo.setSort(sortVos);
        productionPlanDtoResultColPageVo.setColumnTables(columnVos);
        productionPlanDtoResultColPageVo.setPage(Math.toIntExact(planPage.getCurrent()));
        productionPlanDtoResultColPageVo.setSize(Math.toIntExact(planPage.getSize()));
        productionPlanDtoResultColPageVo.setTotal(planPage.getTotal());
        productionPlanDtoResultColPageVo.setPages(planPage.getPages());
        return productionPlanDtoResultColPageVo;
    }

    default List<Object> searchLike(QueryWrapper<Object> queryWrapper) {
        throw new BeneWakeException("当前表不能使用该功能");
    }
}
