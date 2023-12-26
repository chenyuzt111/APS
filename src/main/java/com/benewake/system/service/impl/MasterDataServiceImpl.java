package com.benewake.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.*;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.enums.InterfaceDataType;
import com.benewake.system.entity.enums.MasterDataType;
import com.benewake.system.entity.vo.DownloadViewParams;
import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ResultColPageVo;
import com.benewake.system.entity.vo.ViewColParam;
import com.benewake.system.entity.vo.baseParam.SearchLikeParam;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsColumnTableMapper;
import com.benewake.system.mapper.ApsViewColTableMapper;
import com.benewake.system.service.ApsIntfaceDataServiceBase;
import com.benewake.system.service.ApsMasterDataBaseService;
import com.benewake.system.service.ApsViewTableService;
import com.benewake.system.service.MasterDataService;
import com.benewake.system.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
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
    private Map<String, ApsMasterDataBaseService> masterDataBaseServiceMap;

    @Autowired
    private ApsViewColTableMapper viewColTableMapper;

    @Autowired
    private ApsColumnTableMapper columnTableMapper;

    @Autowired
    private ApsViewTableService viewTableService;

    @Override
    public ResultColPageVo<Object> getFiltrateDate(Integer page, Integer size, QueryViewParams queryViewParams) {
        long l = System.currentTimeMillis();
//        try {
            Integer type = queryViewParams.getTableId();
            ApsMasterDataBaseService masterDataBaseService = getApsMasterDataBaseService(type);

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
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new BeneWakeException("系统内部错误联系管理员" + this.getClass());
//        }
    }

    private ApsMasterDataBaseService getApsMasterDataBaseService(Integer type) {
        MasterDataType masterDataType = MasterDataType.valueOfCode(type);
        if (masterDataType == null) {
            throw new BeneWakeException("type不正确");
        }
        ApsMasterDataBaseService masterDataBaseService = masterDataBaseServiceMap.get(masterDataType.getSeviceName());
        return masterDataBaseService;
    }

    @Override
    public List<Object> searchLike(SearchLikeParam searchLikeParam) {
        Integer type = searchLikeParam.getTableId();
        ApsMasterDataBaseService masterDataBaseService = getApsMasterDataBaseService(type);

        ApsColumnTable columnTable = columnTableMapper.selectById(searchLikeParam.getColId());
        if (columnTable == null) {
            throw new BeneWakeException("colId不存在");
        }
        QueryWrapper<Object> queryWrapper = buildQueryWrapper(searchLikeParam, columnTable);
        List<Object> searchLikeObj = masterDataBaseService.searchLike(queryWrapper);
        if (CollectionUtils.isEmpty(searchLikeObj)) {
            return Collections.emptyList();
        }
        String voColName = columnTable.getVoColName();
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
            ResponseUtil.setFileResp(response, "主数据");
            QueryViewParams queryViewParams = buildQueryViewParams(downloadParam);

            List<Object> list;
            if (downloadParam.getType() == ExcelOperationEnum.ALL_PAGES.getCode()) {
                Long count = getCount(downloadParam);
                ResultColPageVo<Object> pageFiltrate = getFiltrateDate(1, Math.toIntExact(count), queryViewParams);
                list = pageFiltrate.getList();
            } else {
                ResultColPageVo<Object> pageFiltrate = getFiltrateDate(downloadParam.getPage(), downloadParam.getSize(), queryViewParams);
                list = pageFiltrate.getList();
            }

            List<String> colNames = null;
            if (downloadParam.getViewId() != null && downloadParam.getViewId() != -1) {
                colNames = viewTableService.selectColNameByViewId(downloadParam.getViewId());
            }

            if (CollectionUtils.isNotEmpty(list)) {
                EasyExcel.write(response.getOutputStream(), list.get(0).getClass())
                        .sheet("sheet1")
                        .includeColumnFieldNames(colNames)
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                        .doWrite(list);
            } else {
                throw new BeneWakeException("当前数据为空");
            }
        } catch (Exception e) {
            log.error("接口数据表导出失败" + e.getMessage() + downloadParam.getType());
            throw new BeneWakeException("当前数据表导出失败");
        }
    }

    private Long getCount(DownloadViewParams downloadParam) {
        MasterDataType interfaceDataType = MasterDataType.valueOfCode(downloadParam.getTableId());
        if (interfaceDataType == null) {
            throw new BeneWakeException("type找不到");
        }
        ApsMasterDataBaseService masterDataBaseService = masterDataBaseServiceMap.get(interfaceDataType.getSeviceName());
        IService iService = (IService) masterDataBaseService;
        Long count = iService.getBaseMapper().selectCount(null);
        return count;
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
