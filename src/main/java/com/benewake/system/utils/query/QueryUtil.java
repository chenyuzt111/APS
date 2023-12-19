package com.benewake.system.utils.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.benewake.system.entity.ApsColumnTable;
import com.benewake.system.entity.ColumnVo;
import com.benewake.system.entity.SortVo;
import com.benewake.system.entity.ViewColumnDto;
import com.benewake.system.entity.vo.DownloadViewParams;
import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ViewColParam;
import com.benewake.system.entity.vo.baseParam.SearchLikeParam;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.benewake.system.service.scheduling.result.ApsSchedulingResuleBase.queryStrategyFactory;
import static com.benewake.system.utils.BenewakeStringUtils.removeAs;

public class QueryUtil {
    public static QueryWrapper<Object> buildQueryWrapper(List<ViewColumnDto> viewColTables) {
        QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
        for (ViewColumnDto viewColTable : viewColTables) {
            String valueOperator = viewColTable.getValueOperator();
            String enColName = viewColTable.getEnColName();
            String colValue = viewColTable.getColValue();

            if (StringUtils.isNotEmpty(valueOperator) && StringUtils.isNotEmpty(enColName)) {
                queryStrategyFactory.getStrategy(valueOperator)
                        .apply(queryWrapper, removeAs(enColName), colValue);
            }

        }
        return queryWrapper;
    }


    public static QueryWrapper<Object> buildQueryWrapper(SearchLikeParam searchLikeParam, ApsColumnTable columnTable) {
        QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
        String enColName = columnTable.getEnColName();
        queryWrapper
                .like(removeAs(enColName), searchLikeParam.getValue())
                .select(enColName);
        return queryWrapper;
    }

    public static QueryViewParams buildQueryViewParams(DownloadViewParams downloadParam) {
        QueryViewParams queryViewParams = new QueryViewParams();
        queryViewParams.setTableId(downloadParam.getTableId());
        queryViewParams.setViewId(downloadParam.getViewId());
        queryViewParams.setCols(downloadParam.getCols());
        return queryViewParams;
    }

    public static QueryWrapper<Object> buildQueryWrapper(Map<Integer, List<ViewColParam>> colIdMap, Map<Integer, ApsColumnTable> colIdToEnName) {
        QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
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


    public static List<ColumnVo> buildColumnVos(List<ViewColumnDto> viewColTables) {
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


    public static List<SortVo> buildSortVos(List<ViewColumnDto> viewColTables) {
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

}
