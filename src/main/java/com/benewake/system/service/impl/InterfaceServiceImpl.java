package com.benewake.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.ApsTableVersion;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.base.InterfaceDataBase;
import com.benewake.system.entity.enums.InterfaceDataType;
import com.benewake.system.entity.enums.TableVersionState;
import com.benewake.system.entity.vo.PageListRestVo;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.InterfaceService;
import com.benewake.system.service.ApsIntfaceDataServiceBase;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InterfaceServiceImpl implements InterfaceService {

    @Autowired
    private ApsTableVersionService apsTableVersionService;

    @Autowired
    private Map<String, ApsIntfaceDataServiceBase> kingdeeServiceMap;

    @Override
    public List<Object> getMultipleVersionsData(Integer page, Integer size, Integer type) {
        try {
            List<ApsTableVersion> apsTableVersions = getApsTableVersionsLimit5(type);

            List<VersionToChVersion> versionToChVersionArrayList = getVersionToChVersions(apsTableVersions);
            List<Integer> tableVersionList = apsTableVersions.stream().map(ApsTableVersion::getTableVersion).collect(Collectors.toList());
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
                }
            }

            Integer pass = (page - 1) * size;
            return (List<Object>) apsIntfaceDataServiceBase.selectVersionPageList(pass, size, versionToChVersionArrayList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BeneWakeException("系统内部错误联系管理员" + this.getClass());
        }
    }

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
    public PageListRestVo<Object> getAllPage(Integer page, Integer size, Integer type) {
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
            Page resultPage = apsIntfaceDataServiceBase.selectPageList(objectPage, versionToChVersionArrayList);
            PageListRestVo pageListRestVo = new PageListRestVo();
            pageListRestVo.setList(resultPage.getRecords());
            pageListRestVo.setPage(page);
            pageListRestVo.setPages(resultPage.getPages());
            pageListRestVo.setSize(size);
            pageListRestVo.setTotal(resultPage.getTotal());
            return pageListRestVo;
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
