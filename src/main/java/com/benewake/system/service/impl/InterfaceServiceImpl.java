package com.benewake.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.*;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.enums.InterfaceDataType;
import com.benewake.system.entity.enums.TableVersionState;
import com.benewake.system.entity.vo.*;
import com.benewake.system.entity.vo.baseParam.SearchLikeParam;
import com.benewake.system.excel.listener.InterfaceDataListener;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsColumnTableMapper;
import com.benewake.system.mapper.ApsViewColTableMapper;
import com.benewake.system.service.*;
import com.benewake.system.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.benewake.system.utils.query.QueryUtil.*;

@Slf4j
@Service
public class InterfaceServiceImpl implements InterfaceService {

    @Autowired
    private ApsTableVersionService apsTableVersionService;

    @Autowired
    private Map<String, ApsIntfaceDataServiceBase> kingdeeServiceMap;

    @Autowired
    private ApsColumnTableMapper columnTableMapper;

    @Autowired
    private ApsViewColTableMapper viewColTableMapper;

    @Autowired
    private ApsColumnTableService columnTableService;


    @Autowired
    private ApsViewTableService viewTableService;


    private List<VersionToChVersion> getVersionToChVersions(List<ApsTableVersion> apsTableVersions) {
        List<VersionToChVersion> versionToChVersionArrayList = new ArrayList<>();
        Collections.reverse(apsTableVersions);
        int i = 1;
        for (ApsTableVersion apsTableVersion : apsTableVersions) {
            VersionToChVersion versionToChVersion = new VersionToChVersion();
            versionToChVersion.setVersion(apsTableVersion.getTableVersion());
            versionToChVersion.setChVersionName("版本" + i++);
            versionToChVersionArrayList.add(versionToChVersion);
        }
        return versionToChVersionArrayList;
    }


    @Override
    public ResultColPageVo<Object> getPageFiltrate(Integer page, Integer size, QueryViewParams queryViewParams) {
        long l = System.currentTimeMillis();
        try {
            Integer type = Math.toIntExact(queryViewParams.getTableId());
//          获取排程结果表中数据表的版本号最新的五个版本
            List<ApsTableVersion> apsTableVersions = getApsTableVersionsLimit5(type);
            List<Integer> tableVersionList = apsTableVersions.stream()
                    .map(ApsTableVersion::getTableVersion)
                    .collect(Collectors.toList());
//            将版本id转化为版本号
            List<VersionToChVersion> versionToChVersionArrayList = getVersionToChVersions(apsTableVersions);
            InterfaceDataType interfaceDataType = InterfaceDataType.valueOfCode(type);
            if (interfaceDataType == null) {
                throw new BeneWakeException("type不正确");
            }
            ApsIntfaceDataServiceBase apsIntfaceDataServiceBase = kingdeeServiceMap.get(interfaceDataType.getSeviceName());
//          获取数据表中的最新版本号
            Integer latestVersion = getLatestVersion(apsIntfaceDataServiceBase);

//            如果erp表中最后的版本，不在排程成功的五个版本里面则记为即时版本
            if (!tableVersionList.contains(latestVersion)) {
                versionToChVersionArrayList.add(new VersionToChVersion(latestVersion, "须时版本"));
            }

            if (CollectionUtils.isEmpty(versionToChVersionArrayList)) {
                return buildResultColPageVo(page, size);
            }
            VersionToChVersion versionToChVersion = versionToChVersionArrayList.get(versionToChVersionArrayList.size() - 1);
            List<ColumnVo> columnVos = Collections.emptyList();
            List<SortVo> sortVos = Collections.emptyList();
            Long viewId = queryViewParams == null ? null : queryViewParams.getViewId();
            List<ViewColParam> cols = queryViewParams == null ? null : queryViewParams.getCols();
            QueryWrapper<Object> wrapper = new QueryWrapper<>();


            if (cols == null) {
                // 如果是视图拼装视图自带的筛选条件
                if (viewId != null && viewId != -1) {
                    List<ViewColumnDto> viewColTables = viewColTableMapper.getViewColByViewId(viewId, null);
                    columnVos = buildColumnVos(viewColTables);
                    viewColTables = updateMaxVersion(versionToChVersion, viewColTables);
                    wrapper = buildQueryWrapper(viewColTables);
                    // 构造返回给前端的列
                    sortVos = buildSortVos(viewColTables);
                }
                // 构造分页查询条件
            } else {
                // 如果列不是null 那么他有可能是视图 或者全部页 不管是视图还是全部页 都去拼sql
                List<Integer> colIds = cols.stream().map(ViewColParam::getColId).filter(Objects::nonNull).collect(Collectors.toList());
                List<ApsColumnTable> columnTables;
                if (CollectionUtils.isNotEmpty(colIds)) {
                    columnTables = columnTableMapper.selectBatchIds(colIds);
                    Map<Integer, List<ViewColParam>> colIdMap = getIntegerListMap(versionToChVersion, cols, columnTables);
                    Map<Integer, ApsColumnTable> colIdToEnName = columnTables.stream()
                            .collect(Collectors.toMap(ApsColumnTable::getId, x -> x, (existing, replacement) -> existing));
                    wrapper = buildQueryWrapper(colIdMap, colIdToEnName);
                }
            }

            long l2 = System.currentTimeMillis();
            Page resultPage = apsIntfaceDataServiceBase.selectPageLists(new Page<>().setCurrent(page).setSize(size),
                    versionToChVersionArrayList, wrapper);
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


    @Override
    public List<Object> searchLike(SearchLikeParam searchLikeParam) {
        Integer type = searchLikeParam.getTableId();
        List<ApsTableVersion> apsTableVersions = getApsTableVersionsLimit5(type);
        List<Integer> tableVersionList = apsTableVersions.stream()
                .map(ApsTableVersion::getTableVersion)
                .collect(Collectors.toList());
        List<VersionToChVersion> versionToChVersionArrayList = getVersionToChVersions(apsTableVersions);
        InterfaceDataType interfaceDataType = InterfaceDataType.valueOfCode(type);
        if (interfaceDataType == null) {
            throw new BeneWakeException("type不正确");
        }
        ApsIntfaceDataServiceBase intfaceDataServiceBase = kingdeeServiceMap.get(interfaceDataType.getSeviceName());
        Integer latestVersion = getLatestVersion(intfaceDataServiceBase);

        if (!tableVersionList.contains(latestVersion)) {
            versionToChVersionArrayList.add(new VersionToChVersion(latestVersion, "须时版本"));
        }
        ApsColumnTable columnTable = columnTableService.getById(searchLikeParam.getColId());
        if (columnTable == null) {
            throw new BeneWakeException("colId不存在");
        }
        QueryWrapper<Object> queryWrapper = buildQueryWrapper(searchLikeParam, columnTable);
        List<Object> searchLikeObj = intfaceDataServiceBase.searchLike(versionToChVersionArrayList, queryWrapper);
        if (CollectionUtils.isEmpty(searchLikeObj)) {
            return Collections.emptyList();
        }
        String voColName = columnTable.getVoColName();
        return getSearchRes(voColName, searchLikeObj);
    }

//    private QueryWrapper<Object> buildQueryWrapper(SearchLikeParam searchLikeParam, ApsColumnTable columnTable) {
//        QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
//        String enColName = columnTable.getEnColName();
//        queryWrapper
//                .like(removeAs(enColName), searchLikeParam.getValue())
//                .select(enColName);
//        return queryWrapper;
//    }


    private Integer getLatestVersion(ApsIntfaceDataServiceBase apsIntfaceDataServiceBase) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper
                .select("MAX(version) as version")
                .last("limit 1");
        IService iService = (IService) apsIntfaceDataServiceBase;
        Object one = iService.getOne(queryWrapper);
        if (one != null) {
            try {
                Field version = one.getClass().getDeclaredField("version");
                version.setAccessible(true);
                Integer versionIn = (Integer) version.get(one);
                version.setAccessible(false);
                return versionIn;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private ResultColPageVo<Object> buildResultColPageVo(Integer page, Integer size) {
        ResultColPageVo<Object> resultColPageVo = new ResultColPageVo<>();
        resultColPageVo.setList(Collections.emptyList());
        resultColPageVo.setColumnTables(Collections.emptyList());
        resultColPageVo.setPage(page);
        resultColPageVo.setPages(0L);
        resultColPageVo.setSize(size);
        resultColPageVo.setTotal(0L);
        return resultColPageVo;
    }

//    private List<ColumnVo> buildColumnVos(List<ViewColumnDto> viewColTables) {
//        return viewColTables.stream().map(x -> {
//            ColumnVo columnVo = new ColumnVo();
//            columnVo.setId(Math.toIntExact(x.getId()));
//            columnVo.setColId(Math.toIntExact(x.getColId()));
//            columnVo.setChColName(x.getChColName());
//            columnVo.setVoColName(x.getVoColName());
//            String valueOperator = x.getValueOperator();
//            if (valueOperator != null && (valueOperator.equals("ascending") || valueOperator.equals("descending"))) {
//                columnVo.setValueOperator(null);
//                columnVo.setColValue(null);
//            } else {
//                columnVo.setValueOperator(x.getValueOperator());
//                columnVo.setColValue(x.getColValue());
//            }
//            columnVo.setColSeq(x.getColSeq());
//            return columnVo;
//        }).collect(Collectors.toList());
//    }
//
//    private QueryWrapper<Object> buildQueryWrapper(Map<Integer, List<ViewColParam>> colIdMap, Map<Integer, ApsColumnTable> colIdToEnName) {
//        QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
//        colIdMap.forEach((colId, viewColParams) ->
//                viewColParams.forEach(viewColParam -> {
//                    ApsColumnTable apsColumnTable = colIdToEnName.get(colId);
//                    String enColName = apsColumnTable.getEnColName();
//                    String valueOperator = viewColParam.getValueOperator();
//                    String colValue = viewColParam.getColValue();
//
//                    if (StringUtils.isNotEmpty(valueOperator)) {
//                        queryStrategyFactory.getStrategy(valueOperator)
//                                .apply(queryWrapper, removeAs(enColName), colValue);
//                    }
//                }));
//        return queryWrapper;
//    }
//
//    private QueryWrapper<Object> buildQueryWrapper(List<ViewColumnDto> viewColTables) {
//        QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
//        for (ViewColumnDto viewColTable : viewColTables) {
//            String valueOperator = viewColTable.getValueOperator();
//            String enColName = viewColTable.getEnColName();
//            String colValue = viewColTable.getColValue();
//
//            if (StringUtils.isNotEmpty(valueOperator) && StringUtils.isNotEmpty(enColName)) {
//                queryStrategyFactory.getStrategy(valueOperator)
//                        .apply(queryWrapper, removeAs(enColName), colValue);
//            }
//
//        }
//        return queryWrapper;
//    }

    @Override
    public PageResultVo<Object> getAllPage(Integer page, Integer size, Integer type) {
        try {
            List<ApsTableVersion> apsTableVersions = getApsTableVersionsLimit5(type);
            List<Integer> tableVersionList = apsTableVersions.stream()
                    .map(ApsTableVersion::getTableVersion)
                    .collect(Collectors.toList());
            List<VersionToChVersion> versionToChVersionArrayList = getVersionToChVersions(apsTableVersions);
            InterfaceDataType interfaceDataType = InterfaceDataType.valueOfCode(type);
            if (interfaceDataType == null) {
                throw new BeneWakeException("type不正确");
            }
            ApsIntfaceDataServiceBase apsIntfaceDataServiceBase = kingdeeServiceMap.get(interfaceDataType.getSeviceName());
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.orderByDesc("version");
            queryWrapper.last("limit 1");
            IService iService = (IService) apsIntfaceDataServiceBase;
            Object one = iService.getOne(queryWrapper);
            if (one != null) {
                Field version = one.getClass().getDeclaredField("version");
                version.setAccessible(true);
                Integer versionIn = (Integer) version.get(one);
                version.setAccessible(false);
                if (!tableVersionList.contains(versionIn)) {
                    versionToChVersionArrayList.add(new VersionToChVersion(versionIn, "即时版本"));
                    tableVersionList.add(versionIn);
                }
            }

            queryWrapper = new QueryWrapper<>();
            queryWrapper.in("version", tableVersionList);
            Page<Object> objectPage = new Page<>();
            objectPage.setCurrent(page);
            objectPage.setSize(size);
            if (CollectionUtils.isEmpty(versionToChVersionArrayList)) {
                PageResultVo pageResultVo = new PageResultVo();
                pageResultVo.setList(Collections.emptyList());
                pageResultVo.setPage(page);
                pageResultVo.setPages(0L);
                pageResultVo.setSize(size);
                pageResultVo.setTotal(0L);
                return pageResultVo;
            }
            Page resultPage = apsIntfaceDataServiceBase.selectPageList(objectPage, versionToChVersionArrayList);
            PageResultVo pageResultVo = new PageResultVo();
            pageResultVo.setList(resultPage.getRecords());
            pageResultVo.setPage(page);
            pageResultVo.setPages(resultPage.getPages());
            pageResultVo.setSize(size);
            pageResultVo.setTotal(resultPage.getTotal());
            return pageResultVo;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BeneWakeException("系统内部错误联系管理员" + this.getClass());
        }
    }


    @Override
    public Boolean add(String request, Integer type) {
        try {
            InterfaceDataType interfaceDataType = InterfaceDataType.valueOfCode(type);
            if (interfaceDataType == null) {
                throw new BeneWakeException("type找不到");
            }
            Class classs = interfaceDataType.getClasss();
            Object object = JSON.parseObject(request, classs);
            ApsIntfaceDataServiceBase apsIntfaceDataServiceBase = kingdeeServiceMap.get(interfaceDataType.getSeviceName());
            IService iService = (IService) apsIntfaceDataServiceBase;
            QueryWrapper<Object> objectQueryWrapper = new QueryWrapper<>();
            objectQueryWrapper.orderByDesc("version")
                    .last("limit 1");
            Object one = iService.getOne(objectQueryWrapper);
            Integer versionNumber = 1;
            Field version = classs.getDeclaredField("version");
            version.setAccessible(true);
            if (one != null) {
                versionNumber = (Integer) version.get(one);
            }
            version.set(object, versionNumber);
            version.setAccessible(false);
            return iService.save(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Boolean update(String request, Integer type) {
        InterfaceDataType interfaceDataType = InterfaceDataType.valueOfCode(type);
        if (interfaceDataType == null) {
            throw new BeneWakeException("type找不到");
        }
        Class classs = interfaceDataType.getClasss();
        Object object = JSON.parseObject(request, classs);
        ApsIntfaceDataServiceBase apsIntfaceDataServiceBase = kingdeeServiceMap.get(interfaceDataType.getSeviceName());
        IService iService = (IService) apsIntfaceDataServiceBase;
        return iService.updateById(object);
    }

    @Override
    public Boolean delete(List<Integer> ids, Integer type) {
        InterfaceDataType interfaceDataType = InterfaceDataType.valueOfCode(type);
        if (interfaceDataType == null) {
            throw new BeneWakeException("type找不到");
        }
        ApsIntfaceDataServiceBase apsIntfaceDataServiceBase = kingdeeServiceMap.get(interfaceDataType.getSeviceName());
        IService iService = (IService) apsIntfaceDataServiceBase;
        return iService.removeBatchByIds(ids);
    }

    @Override
    public void downloadInterfaceDate(HttpServletResponse response, DownloadViewParams downloadParam) {
        try {
            ResponseUtil.setFileResp(response, "接口数据type");
            QueryViewParams queryViewParams = buildQueryViewParams(downloadParam);

            List<Object> list;
            if (downloadParam.getType() == ExcelOperationEnum.ALL_PAGES.getCode()) {
                Long count = getCount(downloadParam);
                ResultColPageVo<Object> pageFiltrate = getPageFiltrate(1, Math.toIntExact(count), queryViewParams);
                list = pageFiltrate.getList();
            } else {
                ResultColPageVo<Object> pageFiltrate = getPageFiltrate(downloadParam.getPage(), downloadParam.getSize(), queryViewParams);
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
        InterfaceDataType interfaceDataType = InterfaceDataType.valueOfCode(downloadParam.getTableId());
        if (interfaceDataType == null) {
            throw new BeneWakeException("type找不到");
        }
        ApsIntfaceDataServiceBase apsIntfaceDataServiceBase = kingdeeServiceMap.get(interfaceDataType.getSeviceName());
        IService iService = (IService) apsIntfaceDataServiceBase;
        Long count = iService.getBaseMapper().selectCount(null);
        return count;
    }


    @Override
    public void downloadInterfaceTemplate(Integer type, HttpServletResponse response) {
        InterfaceDataType interfaceDataType = InterfaceDataType.valueOfCode(type);
        if (interfaceDataType == null) {
            throw new BeneWakeException("type类型不存在");
        }
        Class importTemplate = interfaceDataType.getClasss();
        try {
            ResponseUtil.setFileResp(response, "接口导出模板");
            EasyExcel.write(response.getOutputStream(), importTemplate).sheet("sheet1").doWrite((Collection<?>) null);
        } catch (Exception e) {
            log.error("接口导出模板下载出错 type =" + type + "-----" + e.getMessage() + "----" + LocalDateTime.now());
        }
    }

    @Override
    public Boolean importInterfaceData(Integer code, Integer type, MultipartFile file) {
        try {
            InterfaceDataType interfaceDataType = InterfaceDataType.valueOfCode(code);
            String serviceNameOfCode = interfaceDataType.getSeviceName();
            ApsIntfaceDataServiceBase apsIntfaceDataServiceBase = kingdeeServiceMap.get(serviceNameOfCode);
            EasyExcel.read(file.getInputStream(), new InterfaceDataListener(code, apsIntfaceDataServiceBase, type))
                    .sheet(0).headRowNumber(1).doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    //获取排程成功的最近的五个版本
    private List<ApsTableVersion> getApsTableVersionsLimit5(Integer type) {
        LambdaQueryWrapper<ApsTableVersion> apsTableVersionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsTableVersionLambdaQueryWrapper.eq(ApsTableVersion::getTableId, type)
                .eq(ApsTableVersion::getState, TableVersionState.SUCCESS.getCode())
                .select(ApsTableVersion::getTableVersion)
                .orderByDesc(ApsTableVersion::getVersionNumber)
                .last("limit 5");

        List<ApsTableVersion> apsTableVersions = apsTableVersionService.list(apsTableVersionLambdaQueryWrapper);
        apsTableVersions = apsTableVersions.stream().distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(apsTableVersions)) {
            Collections.reverse(apsTableVersions);
        }
        return apsTableVersions;
    }
}
