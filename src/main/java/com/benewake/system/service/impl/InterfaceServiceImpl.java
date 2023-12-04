package com.benewake.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.ApsTableVersion;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.base.InterfaceDataBase;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.enums.InterfaceDataType;
import com.benewake.system.entity.enums.TableVersionState;
import com.benewake.system.entity.vo.DownloadParam;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.excel.listener.InterfaceDataListener;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsIntfaceDataServiceBase;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.InterfaceService;
import com.benewake.system.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InterfaceServiceImpl implements InterfaceService {

    @Autowired
    private ApsTableVersionService apsTableVersionService;

    @Autowired
    private Map<String, ApsIntfaceDataServiceBase> kingdeeServiceMap;


    private List<VersionToChVersion> getVersionToChVersions(List<ApsTableVersion> apsTableVersions) {
        List<VersionToChVersion> versionToChVersionArrayList = new ArrayList<>();
        int i = 1;
        for (ApsTableVersion apsTableVersion : apsTableVersions) {
            VersionToChVersion versionToChVersion = new VersionToChVersion();
            versionToChVersion.setVersion(apsTableVersion.getTableVersion());
            versionToChVersion.setChVersionName("版本" + i++);
            versionToChVersionArrayList.add(versionToChVersion);
        }
        return versionToChVersionArrayList;
    }

    private Map<Integer, String> getVersionToChVersionsMap(List<ApsTableVersion> apsTableVersions) {
        Map<Integer, String> versionToChVersionMap = new HashMap<>();
        int i = 1;
        for (ApsTableVersion apsTableVersion : apsTableVersions) {
            versionToChVersionMap.put(apsTableVersion.getTableVersion(), "版本" + i++);
        }
        return versionToChVersionMap;
    }

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
    public void downloadProcessCapacity(HttpServletResponse response, Integer type, DownloadParam downloadParam) {
        try {
            ResponseUtil.setFileResp(response, "接口数据type1");
            List<Object> list = null;
            if (downloadParam.getType() == ExcelOperationEnum.ALL_PAGES.getCode()) {
                InterfaceDataType interfaceDataType = InterfaceDataType.valueOfCode(type);
                if (interfaceDataType == null) {
                    throw new BeneWakeException("type找不到");
                }
                ApsIntfaceDataServiceBase apsIntfaceDataServiceBase = kingdeeServiceMap.get(interfaceDataType.getSeviceName());
                IService iService = (IService) apsIntfaceDataServiceBase;
                Long count = iService.getBaseMapper().selectCount(null);
                PageResultVo<Object> objectPageResultVo = getAllPage(1, Math.toIntExact(count), type);
                list = objectPageResultVo.getList();
            } else {
                PageResultVo<Object> objectPageResultVo = getAllPage(downloadParam.getPage(), downloadParam.getSize(), type);
                list = objectPageResultVo.getList();
            }
            if (CollectionUtils.isNotEmpty(list)) {
                EasyExcel.write(response.getOutputStream(), list.get(0)
                        .getClass()).sheet("sheet1").doWrite(list);
            } else {
                throw new BeneWakeException("当前数据为空");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void downloadInterfaceTemplate(Integer type, HttpServletResponse response) {
        InterfaceDataType interfaceDataType = InterfaceDataType.valueOfCode(type);
        if (interfaceDataType == null) {
            throw new BeneWakeException("type类型不存在");
        }
        Class importTemplate = interfaceDataType.getClasss();
        try {
            ResponseUtil.setFileResp(response , "接口导出模板");
            EasyExcel.write(response.getOutputStream(), importTemplate).sheet("sheet1").doWrite((Collection<?>) null);
        } catch (Exception e) {
            log.error("接口导出模板下载出错 type =" + type + "-----" + e.getMessage() + "----" + LocalDateTime.now());
        }
    }

    @Override
    public Boolean importInterfaceData(Integer code, Integer type, MultipartFile file) {
        try {
            InterfaceDataType interfaceDataType = InterfaceDataType.valueOfCode(code);
            String serviceNameOfCode =  interfaceDataType.getSeviceName();
            ApsIntfaceDataServiceBase apsIntfaceDataServiceBase = kingdeeServiceMap.get(serviceNameOfCode);
            EasyExcel.read(file.getInputStream() ,new InterfaceDataListener(code ,apsIntfaceDataServiceBase ,type))
                    .sheet(0).headRowNumber(1).doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private List<InterfaceDataBase> convertRecordsToRestlt(List records, Map<Integer, String> versionToChVersions) {
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }
        ArrayList<InterfaceDataBase> interfaceDataBases = null;
        try {
            interfaceDataBases = new ArrayList<>(records.size());
            for (Object record : records) {
                InterfaceDataBase interfaceDataBase = new InterfaceDataBase();
                interfaceDataBase.setResult(record);
                Field version = record.getClass().getDeclaredField("version");
                version.setAccessible(true);
                Integer o = (Integer) version.get(record);
                version.setAccessible(false);
                interfaceDataBase.setChVersion(versionToChVersions.getOrDefault(o, String.valueOf(o)));
                interfaceDataBases.add(interfaceDataBase);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return interfaceDataBases;
    }

    private List<ApsTableVersion> getApsTableVersionsLimit5(Integer type) {
        //取出前5版本的version
        LambdaQueryWrapper<ApsTableVersion> apsTableVersionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsTableVersionLambdaQueryWrapper.eq(ApsTableVersion::getTableId, type)
                .eq(ApsTableVersion::getState, TableVersionState.SUCCESS.getCode())
                .orderByDesc(ApsTableVersion::getVersionNumber)
                .last("limit 5");

        List<ApsTableVersion> apsTableVersions = apsTableVersionService.getBaseMapper().selectList(apsTableVersionLambdaQueryWrapper);
        apsTableVersions = apsTableVersions.stream().distinct().collect(Collectors.toList());
        if (apsTableVersions != null) {
            Collections.reverse(apsTableVersions);
        }
        return apsTableVersions;
    }
}
