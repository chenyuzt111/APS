package com.benewake.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.*;
import com.benewake.system.entity.dto.ApsDailyDataUploadDto;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.vo.*;
import com.benewake.system.entity.vo.baseParam.SearchLikeParam;
import com.benewake.system.excel.entity.ExcelDailyDataUploadTemplate;
import com.benewake.system.excel.listener.ExcelDailyDataUploadListener;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsColumnTableMapper;
import com.benewake.system.mapper.ApsDailyDataUploadMapper;
import com.benewake.system.mapper.ApsViewColTableMapper;
import com.benewake.system.service.ApsDailyDataUploadService;
import com.benewake.system.service.ApsProcessNamePoolService;
import com.benewake.system.service.ApsViewTableService;
import com.benewake.system.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.benewake.system.service.scheduling.result.ApsSchedulingResuleBase.queryStrategyFactory;
import static com.benewake.system.utils.BenewakeStringUtils.removeAs;
import static com.benewake.system.utils.query.QueryUtil.*;

/**
 * @author ASUS
 * @description 针对表【aps_daily_data_upload】的数据库操作Service实现
 * @createDate 2023-11-10 14:52:13
 */
@Slf4j
@Service
public class ApsDailyDataUploadServiceImpl extends ServiceImpl<ApsDailyDataUploadMapper, ApsDailyDataUpload>
        implements ApsDailyDataUploadService {

    @Autowired
    private ApsDailyDataUploadMapper dailyDataUploadMapper;

    @Autowired
    private ApsProcessNamePoolService processNamePoolService;

    @Autowired
    private ApsViewColTableMapper viewColTableMapper;

    @Autowired
    private ApsColumnTableMapper columnTableMapper;

    @Autowired
    private ApsViewTableService viewTableService;


    @Override
    public PageResultVo<ApsDailyDataUploadDto> getDailyDataListPage(Integer page, Integer size) {
        Page<ApsDailyDataUploadDto> uploadPage = new Page<>();
        uploadPage.setCurrent(page);
        uploadPage.setSize(size);
        Page<ApsDailyDataUploadDto> dailyDataUploadPage = dailyDataUploadMapper.selectPageList(uploadPage);
        return getUploadPageListRestVo(dailyDataUploadPage);
    }

    @Override
    public void downloadDailyData(HttpServletResponse response, DownloadViewParams downloadParam) {
        try {
            ResponseUtil.setFileResp(response, "日别数据导出");
            List<ApsDailyDataUploadDto> dataListPageList;
            QueryViewParams queryViewParams = buildQueryViewParams(downloadParam);
            if (downloadParam.getType() == ExcelOperationEnum.ALL_PAGES.getCode()) {
                Long count = getBaseMapper().selectCount(null);
                ResultColPageVo<ApsDailyDataUploadDto> dailyDataListPage = getDailyDataFilter(1, Math.toIntExact(count), queryViewParams);
                dataListPageList = dailyDataListPage.getList();
            } else {
                ResultColPageVo<ApsDailyDataUploadDto> dailyDataListPage = getDailyDataFilter(downloadParam.getPage(),
                        downloadParam.getSize(), queryViewParams);
                dataListPageList = dailyDataListPage.getList();
            }

            List<String> colNames = null;
            if (downloadParam.getViewId() != null) {
                colNames = viewTableService.selectColNameByViewId(downloadParam.getViewId());
            }

            EasyExcel.write(response.getOutputStream(), ApsDailyDataUploadDto.class)
                    .sheet("sheet1")
                    .includeColumnFieldNames(colNames)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .doWrite(dataListPageList);

        } catch (Exception e) {
            log.error("导出日别数据失败---" + LocalDateTime.now() + e.getMessage());
            throw new BeneWakeException("导出日别数据失败");
        }
    }

    @Override
    public ResultColPageVo<ApsDailyDataUploadDto> getDailyDataFilter(Integer page, Integer size, QueryViewParams queryViewParams) {
        List<ColumnVo> columnVos = Collections.emptyList();
        List<SortVo> sortVos = Collections.emptyList();
        Long viewId = queryViewParams == null ? null : queryViewParams.getViewId();
        List<ViewColParam> cols = queryViewParams == null ? null : queryViewParams.getCols();
        QueryWrapper<Object> wrapper = null;
        if (CollectionUtils.isEmpty(cols)) {
            // 如果是视图拼装视图自带的筛选条件
            if (viewId != null) {
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
                    .collect(Collectors.groupingBy(ViewColParam::getColId));
            Map<Integer, ApsColumnTable> colIdToEnName = columnTables.stream()
                    .collect(Collectors.toMap(ApsColumnTable::getId, x -> x, (existing, replacement) -> existing));
            wrapper = buildQueryWrapper(colIdMap, colIdToEnName);
        }
        wrapper = wrapper == null ? new QueryWrapper<>() : wrapper;
        String customSqlSegment = wrapper.getCustomSqlSegment();
        if (StringUtils.isEmpty(customSqlSegment) || !customSqlSegment.contains("ORDER BY")) {
            wrapper.orderByDesc("f_order_number ");
            wrapper.orderByAsc("process_name");
        }
        Page<ApsDailyDataUploadDto> resultPage = dailyDataUploadMapper.selectPageLists(new Page<>().setCurrent(page).setSize(size), wrapper);

        return buildDailyDataUploadDtoResultColPageVo(columnVos, sortVos, resultPage);
    }

    private ResultColPageVo<ApsDailyDataUploadDto> buildDailyDataUploadDtoResultColPageVo(List<ColumnVo> columnVos, List<SortVo> sortVos, Page<ApsDailyDataUploadDto> resultPage) {
        ResultColPageVo<ApsDailyDataUploadDto> resultColPageVo = new ResultColPageVo<>();
        resultColPageVo.setList(resultPage.getRecords());
        resultColPageVo.setColumnTables(columnVos);
        resultColPageVo.setSort(sortVos);
        resultColPageVo.setPage(Math.toIntExact(resultPage.getCurrent()));
        resultColPageVo.setSize(Math.toIntExact(resultPage.getSize()));
        resultColPageVo.setTotal(resultPage.getTotal());
        resultColPageVo.setPages(resultPage.getPages());
        return resultColPageVo;
    }

    @Override
    public List<Object> searchLike(SearchLikeParam searchLikeParam) {
        ApsColumnTable columnTable = columnTableMapper.selectById(searchLikeParam.getColId());
        if (columnTable == null) {
            throw new BeneWakeException("colId不存在");
        }
        QueryWrapper<Object> queryWrapper = buildQueryWrapper(searchLikeParam, columnTable);
        List<Object> searchLikeObj = dailyDataUploadMapper.searchLike(queryWrapper);
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
    public Boolean importloadDailyData(Integer type, MultipartFile file) {
        try {
            ExcelReader excelReader = EasyExcel.read(file.getInputStream(), ExcelDailyDataUploadTemplate.class,
                    new ExcelDailyDataUploadListener(this, type, processNamePoolService)).build();
            ReadSheet readSheet = EasyExcel.readSheet(0).build();
            excelReader.read(readSheet);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BeneWakeException) {
                log.warn("导入日别数据失败" + LocalDateTime.now() + e.getMessage());
                throw new BeneWakeException(e.getMessage());
            }
            log.error("导入日别数据失败" + LocalDateTime.now() + e.getMessage());
            throw new BeneWakeException("导入日别数据失败未知异常");
        }
    }

    @Override
    public Boolean addOrUpdateDailyData(ApsDailyDataUploadParam dailyDataUploadParam) {
        ApsDailyDataUpload apsDailyDataUpload = dailDataParamToPo(dailyDataUploadParam);
        boolean res;
        if (dailyDataUploadParam.getId() == null) {
            res = save(apsDailyDataUpload);
        } else {
            res = updateById(apsDailyDataUpload);
        }
        return res;
    }

    @Override
    public Boolean removeByIdList(List<Integer> ids) {
        removeBatchByIds(ids);

        return true;
    }

    private ApsDailyDataUpload dailDataParamToPo(ApsDailyDataUploadParam dailyDataUploadParam) {
        ApsDailyDataUpload apsDailyDataUpload = new ApsDailyDataUpload();
        apsDailyDataUpload.setId(dailyDataUploadParam.getId());
        apsDailyDataUpload.setFOrderNumber(dailyDataUploadParam.getOrderNumber());
        apsDailyDataUpload.setFMaterialCode(dailyDataUploadParam.getMaterialCode());
        apsDailyDataUpload.setFProcessId(dailyDataUploadParam.getProcessId());
        apsDailyDataUpload.setFTotalQuantity(dailyDataUploadParam.getTotalQuantity());
        apsDailyDataUpload.setFCompletedQuantity(dailyDataUploadParam.getCompletedQuantity());
        apsDailyDataUpload.setFCapacityPsPuPp(dailyDataUploadParam.getCapacityPsPuPp());
        apsDailyDataUpload.setFRemainingQuantity(dailyDataUploadParam.getRemainingQuantity());
        apsDailyDataUpload.setFRemainingCapacity(dailyDataUploadParam.getRemainingCapacity());
        return apsDailyDataUpload;
    }

    private PageResultVo<ApsDailyDataUploadDto> getUploadPageListRestVo(Page<ApsDailyDataUploadDto> dailyDataUploadPage) {
        PageResultVo<ApsDailyDataUploadDto> pageResultVo = new PageResultVo<>();
        pageResultVo.setList(dailyDataUploadPage.getRecords());
        pageResultVo.setSize(Math.toIntExact(dailyDataUploadPage.getSize()));
        pageResultVo.setPages(dailyDataUploadPage.getPages());
        pageResultVo.setTotal(dailyDataUploadPage.getTotal());
        pageResultVo.setPage(Math.toIntExact(dailyDataUploadPage.getCurrent()));
        return pageResultVo;
    }


    @Override
    public void InsertDataIntoApsFimRequest(int a) {
        dailyDataUploadMapper.callInsertDataIntoApsFimRequest(a);
    }


}




