package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.*;
import com.benewake.system.entity.enums.MasterDataType;
import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ResultColPageVo;
import com.benewake.system.entity.vo.ViewColParam;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsColumnTableMapper;
import com.benewake.system.mapper.ApsViewColTableMapper;
import com.benewake.system.service.ApsMasterDataBaseService;
import com.benewake.system.service.MasterDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.benewake.system.utils.query.QueryUtil.*;
import static com.benewake.system.utils.query.QueryUtil.buildQueryWrapper;

@Slf4j
@Service
public class MasterDataServiceImpl implements MasterDataService {

    @Autowired
    private Map<String , ApsMasterDataBaseService> masterDataBaseServiceMap;

    @Autowired
    private ApsViewColTableMapper viewColTableMapper;

    @Autowired
    private ApsColumnTableMapper columnTableMapper;
    @Override
    public ResultColPageVo<Object> getFiltrateDate(Integer page, Integer size, QueryViewParams queryViewParams) {
        long l = System.currentTimeMillis();
        try {
            Integer type = queryViewParams.getTableId();
            MasterDataType masterDataType = MasterDataType.valueOfCode(type);
            if (masterDataType == null) {
                throw new BeneWakeException("type不正确");
            }
            ApsMasterDataBaseService masterDataBaseService = masterDataBaseServiceMap.get(masterDataType.getSeviceName());

            List<ColumnVo> columnVos = Collections.emptyList();
            List<SortVo> sortVos = Collections.emptyList();
            Long viewId = queryViewParams == null ? null : queryViewParams.getViewId();
            List<ViewColParam> cols = queryViewParams == null ? null : queryViewParams.getCols();
            QueryWrapper<Object> wrapper = null;
            if (CollectionUtils.isEmpty(cols)) {
                // 如果是视图拼装视图自带的筛选条件
                if (viewId != null && viewId != -1) {
                    List<ViewColumnDto> viewColTables = viewColTableMapper.getViewColByViewId(viewId, null);
                    wrapper = buildQueryWrapper(viewColTables);
                    // 构造返回给前端的列
                    columnVos = buildColumnVos(viewColTables);
                    sortVos = buildSortVos(viewColTables);
                }
                // 构造分页查询条件
            } else {
                // 如果列不是null 那么他有可能是视图 或者全部页 不管是视图还是全部页 都去拼sql
                List<Integer> colIds = cols.stream().map(ViewColParam::getColId).collect(Collectors.toList());
                List<ApsColumnTable> columnTables = columnTableMapper.selectBatchIds(colIds);
                Map<Integer, List<ViewColParam>> colIdMap = cols.stream()
                        .filter(x -> x.getColId() != null)
                        .collect(Collectors.groupingBy(ViewColParam::getColId));
                Map<Integer, ApsColumnTable> colIdToEnName = columnTables.stream()
                        .collect(Collectors.toMap(ApsColumnTable::getId, x -> x, (existing, replacement) -> existing));
                wrapper = buildQueryWrapper(colIdMap, colIdToEnName);
            }

            long l2 = System.currentTimeMillis();
            Page resultPage = masterDataBaseService.selectPageLists(new Page<>().setCurrent(page).setSize(size), wrapper);
            long l3 = System.currentTimeMillis();
            log.info("查询耗时：" + (l3 - l2) + "ms");
            ResultColPageVo<Object> resultColPageVo = buildResultColPageVo(columnVos, sortVos, resultPage);
            long l1 = System.currentTimeMillis();
            log.info("总耗时：" + (l1 - l) + "ms");
            return resultColPageVo;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BeneWakeException("系统内部错误联系管理员" + this.getClass());
        }
    }
    private ResultColPageVo<Object> buildResultColPageVo(List<ColumnVo> columnVos, List<SortVo> sortVos, Page resultPage) {
        ResultColPageVo<Object> resultColPageVo = new ResultColPageVo<>();
        resultColPageVo.setList(resultPage.getRecords());
        resultColPageVo.setColumnTables(columnVos);
        resultColPageVo.setSort(sortVos);
        resultColPageVo.setPage(Math.toIntExact(resultPage.getCurrent()));
        resultColPageVo.setSize(Math.toIntExact(resultPage.getSize()));
        resultColPageVo.setTotal(resultPage.getTotal());
        resultColPageVo.setPages(resultPage.getPages());
        return resultColPageVo;
    }


}
