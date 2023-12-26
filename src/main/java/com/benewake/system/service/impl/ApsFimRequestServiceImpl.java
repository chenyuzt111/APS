package com.benewake.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.*;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.enums.SchedulingResultType;
import com.benewake.system.entity.vo.*;
import com.benewake.system.entity.vo.baseParam.SearchLikeParam;
import com.benewake.system.excel.entity.ExcelFimRequest;
import com.benewake.system.excel.entity.ExcelFimRequestTemplate;
import com.benewake.system.excel.listener.FimRequestListener;
import com.benewake.system.excel.transfer.FimRequestVoTiExcelList;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsColumnTableMapper;
import com.benewake.system.mapper.ApsFimRequestMapper;
import com.benewake.system.mapper.ApsViewColTableMapper;
import com.benewake.system.service.ApsFimRequestService;
import com.benewake.system.service.ApsViewTableService;
import com.benewake.system.service.scheduling.result.ApsSchedulingResuleBase;
import com.benewake.system.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.benewake.system.utils.query.QueryUtil.*;
import static com.benewake.system.utils.query.QueryUtil.buildQueryWrapper;

/**
 * @author ASUS
 * @description 针对表【aps_fim_request】的数据库操作Service实现
 * @createDate 2023-12-01 09:34:02
 */
@Slf4j
@Service
public class ApsFimRequestServiceImpl extends ServiceImpl<ApsFimRequestMapper, ApsFimRequest>
        implements ApsFimRequestService {

    @Autowired
    private ApsFimRequestMapper fimRequestMapper;

    @Autowired
    private FimRequestVoTiExcelList fimRequestVoTiExcelList;

    @Autowired
    private ApsViewColTableMapper viewColTableMapper;

    @Autowired
    private ApsColumnTableMapper columnTableMapper;

    @Autowired
    private ApsViewTableService viewTableService;


    @Override
    public PageResultVo<ApsFimRequestVo> getFimRequestPage(Integer page, Integer size) {
        Page<ApsFimRequestVo> pageParam = new Page<>();
        pageParam.setSize(size);
        pageParam.setCurrent(page);

        Page<ApsFimRequestVo> fimRequestVoPage = fimRequestMapper.getFimRequestPage(pageParam);
        return buildPageListVo(fimRequestVoPage);
    }

    @Override
    public Boolean addOrUpdateFimRequest(ApsFimRequestParam fimRequestParam) {
        ApsFimRequest fimRequest = builFimRequestPo(fimRequestParam);
        if (fimRequestParam.getId() == null) {
            return save(fimRequest);
        } else {
            return updateById(fimRequest);
        }
    }

    @Override
    public Page selectPageLists(Page page, List versionToChVersionArrayList, QueryWrapper wrapper) {
        String customSqlSegment = wrapper.getCustomSqlSegment();
        if (StringUtils.isEmpty(customSqlSegment) || !customSqlSegment.contains("ORDER BY")) {
            wrapper.orderByDesc("ch_version_name");
            wrapper.orderByAsc("f_document_number");
            wrapper.orderByAsc("f_document_type");
        }
        return fimRequestMapper.selectPageLists(page ,versionToChVersionArrayList, wrapper);
    }

    @Override
    public List searchLike(List versionToChVersionArrayList, QueryWrapper queryWrapper) {
        return fimRequestMapper.searchLike(versionToChVersionArrayList,queryWrapper);
    }

//    @Override
//    public void downloadFimRequest(HttpServletResponse response, DownloadViewParams downloadParam) {
//        try {
//            QueryViewParams queryViewParams = buildQueryViewParams(downloadParam);
//            ResultColPageVo<ApsFimRequestVo> resultFiltrate;
//            if (downloadParam.getType() == ExcelOperationEnum.ALL_PAGES.getCode()) {
//                Integer size = Math.toIntExact(this.count());
//                resultFiltrate = this
//                        .getFimRequestPageFilter(1, size, queryViewParams);
//            } else {
//                resultFiltrate = this
//                        .getFimRequestPageFilter(downloadParam.getPage(), downloadParam.getSize(), queryViewParams);
//            }
//            List<String> colNames = null;
//            if (downloadParam.getViewId() != null) {
//                colNames = viewTableService.selectColNameByViewId(downloadParam.getViewId());
//            }
//            List<ApsFimRequestVo> resList = resultFiltrate.getList();
//            ResponseUtil.setFileResp(response, "排程结果导出");
//            if (CollectionUtils.isNotEmpty(resList)) {
//                EasyExcel.write(response.getOutputStream(), resList.get(0).getClass())
//                        .sheet("sheet1")
//                        .includeColumnFieldNames(colNames)
//                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
//                        .doWrite(resList);
//            } else {
//                throw new BeneWakeException("当前数据为空");
//            }
//        } catch (Exception e) {
//            if (!(e instanceof BeneWakeException)) {
//                log.error("排程结果导出失败" + e.getMessage());
//                throw new BeneWakeException("排程结果导出失败");
//            }
//            log.info("排程结果数据为空" + e.getMessage());
//            throw new BeneWakeException(e.getMessage());
//        }
//    }

    @Override
    public Boolean saveDataByExcel(Integer type, MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), ExcelFimRequestTemplate.class, new FimRequestListener(this, type))
                    .sheet().headRowNumber(1).doRead();
        } catch (Exception e) {
            log.error("导入fim需求表失败" + e.getMessage());
            if (e instanceof BeneWakeException) {
                throw new BeneWakeException(e.getMessage());
            }
            throw new BeneWakeException("导入fim需求表失败");
        }
        return true;
    }

//    @Override
//    public ResultColPageVo<ApsFimRequestVo> getFimRequestPageFilter(Integer page, Integer size,
//                                                                    QueryViewParams queryViewParams) {
//
//        List<ColumnVo> columnVos = Collections.emptyList();
//        List<SortVo> sortVos = Collections.emptyList();
//        Long viewId = queryViewParams == null ? null : queryViewParams.getViewId();
//        List<ViewColParam> cols = queryViewParams == null ? null : queryViewParams.getCols();
//        QueryWrapper<Object> wrapper = null;
//        if (CollectionUtils.isEmpty(cols)) {
//            // 如果是视图拼装视图自带的筛选条件
//            if (viewId != null) {
//                List<ViewColumnDto> viewColTables = viewColTableMapper.getViewColByViewId(viewId, null);
//                wrapper = buildQueryWrapper(viewColTables);
//                // 构造返回给前端的列
//                columnVos = buildColumnVos(viewColTables);
//                sortVos = buildSortVos(viewColTables);
//            }
//            // 构造分页查询条件
//        } else {
//            // 如果列不是null 那么他有可能是视图 或者全部页 不管是视图还是全部页 都去拼sql
//            List<Integer> colIds = cols.stream().map(ViewColParam::getColId).collect(Collectors.toList());
//            List<ApsColumnTable> columnTables = columnTableMapper.selectBatchIds(colIds);
//            Map<Integer, List<ViewColParam>> colIdMap = cols.stream()
//                    .collect(Collectors.groupingBy(ViewColParam::getColId));
//            Map<Integer, ApsColumnTable> colIdToEnName = columnTables.stream()
//                    .collect(Collectors.toMap(ApsColumnTable::getId, x -> x, (existing, replacement) -> existing));
//            wrapper = buildQueryWrapper(colIdMap, colIdToEnName);
//        }
//        QueryWrapper<ApsFimRequest> fimQueryWrapper = new QueryWrapper<>();
//        fimQueryWrapper.select("max(version) as version");
//        ApsFimRequest fimRequest = fimRequestMapper.selectOne(fimQueryWrapper);
//        if (fimRequest == null || fimRequest.getVersion() == null) {
//            return null;
//        }
//        wrapper.eq("version", fimRequest.getVersion());
//        Page<ApsFimRequestVo> resultPage = fimRequestMapper.selectPageLists(new Page<>().setCurrent(page).setSize(size), wrapper);
//
//        return buildDailyDataUploadDtoResultColPageVo(columnVos, sortVos, resultPage);
//    }

//    @Override
//    public List<Object> searchLike(SearchLikeParam searchLikeParam) {
//        ApsColumnTable columnTable = columnTableMapper.selectById(searchLikeParam.getColId());
//        if (columnTable == null) {
//            throw new BeneWakeException("colId不存在");
//        }
//        QueryWrapper<Object> queryWrapper = buildQueryWrapper(searchLikeParam, columnTable);
//        List<Object> searchLikeObj = fimRequestMapper.searchLike(queryWrapper);
//        if (CollectionUtils.isEmpty(searchLikeObj)) {
//            return Collections.emptyList();
//        }
//        String voColName = columnTable.getVoColName();
//        return searchLikeObj.stream().map(x -> {
//            Object searchLike;
//            try {
//                Field declaredField = x.getClass().getDeclaredField(voColName);
//                declaredField.setAccessible(true);
//                searchLike = declaredField.get(x);
//                declaredField.setAccessible(false);
//            } catch (NoSuchFieldException | IllegalAccessException e) {
//                log.error("反射发生异常{}", e.getMessage());
//                throw new BeneWakeException("服务器资源问题");
//            }
//            return searchLike;
//        }).sorted().collect(Collectors.toList());
//    }

    private ResultColPageVo<ApsFimRequestVo> buildDailyDataUploadDtoResultColPageVo(List<ColumnVo> columnVos, List<SortVo> sortVos, Page<ApsFimRequestVo> resultPage) {
        ResultColPageVo<ApsFimRequestVo> resultColPageVo = new ResultColPageVo<>();
        resultColPageVo.setList(resultPage.getRecords());
        resultColPageVo.setColumnTables(columnVos);
        resultColPageVo.setSort(sortVos);
        resultColPageVo.setPage(Math.toIntExact(resultPage.getCurrent()));
        resultColPageVo.setSize(Math.toIntExact(resultPage.getSize()));
        resultColPageVo.setTotal(resultPage.getTotal());
        resultColPageVo.setPages(resultPage.getPages());
        return resultColPageVo;
    }


    private ApsFimRequest builFimRequestPo(ApsFimRequestParam fimRequestParam) {
        if (fimRequestParam == null) {
            return null;
        }
        ApsFimRequest fimRequest = new ApsFimRequest();
        fimRequest.setId(fimRequestParam.getId());
        fimRequest.setFDocumentNumber(fimRequestParam.getDocumentNumber());
        fimRequest.setFCreator(fimRequestParam.getFCreator());
        fimRequest.setFMaterialCode(fimRequestParam.getMaterialCode());
        fimRequest.setFCustomerName(fimRequestParam.getCustomerName());
        fimRequest.setFSalesperson(fimRequestParam.getSalesperson());
        fimRequest.setFQuantity(fimRequestParam.getQuantity());
        fimRequest.setFExpectedDeliveryDate(fimRequestParam.getExpectedDeliveryDate());
        fimRequest.setFDocumentType(fimRequestParam.getDocumentType());
        return fimRequest;
    }

    private PageResultVo<ApsFimRequestVo> buildPageListVo(Page<ApsFimRequestVo> fimRequestVoPage) {
        PageResultVo<ApsFimRequestVo> listRestVo = new PageResultVo<>();
        listRestVo.setList(fimRequestVoPage.getRecords());
        listRestVo.setPage(Math.toIntExact(fimRequestVoPage.getCurrent()));
        listRestVo.setSize(Math.toIntExact(fimRequestVoPage.getSize()));
        listRestVo.setTotal(fimRequestVoPage.getTotal());
        listRestVo.setPages(fimRequestVoPage.getPages());
        return listRestVo;
    }
}




