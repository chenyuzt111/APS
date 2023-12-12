package com.benewake.system.service.impl;

import com.benewake.system.entity.enums.InterfaceDataType;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsFinalUnfinishedDataMapper;
import com.benewake.system.service.ApsIntfaceDataServiceBase;
import com.benewake.system.service.InterfaceDataService;
import com.benewake.system.utils.threadpool.BenewakeExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InterfaceDataServiceImpl implements InterfaceDataService {

    @Autowired
    private Map<String, ApsIntfaceDataServiceBase> kingdeeServiceMap;

    @Autowired
    private ApsFinalUnfinishedDataMapper finalUnfinishedDataMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateData(List<Integer> ids) {
        Map<Integer, Future> codeToFutureMap = new HashMap<>();
//        for (Integer code : ids) {
        ids.forEach(code -> {
            Future<Boolean> future = BenewakeExecutor.submit(() -> {
                String serviceName = InterfaceDataType.serviceNameOfCode(code);
                if (StringUtils.isBlank(serviceName)) {
                    throw new BeneWakeException("id为：" + code + "未找到对应service"); // 如果 serviceName 为空，跳过当前循环
                }

                ApsIntfaceDataServiceBase service = kingdeeServiceMap.get(serviceName);
                if (service == null) {
                    throw new BeneWakeException(serviceName + "未找到");// 如果未找到对应的服务，跳过当前循环
                }

                Boolean res = service.updateDataVersions();
                if (!res) {
                    throw new BeneWakeException(serviceName + "数据库更新异常");
                }
                return true;
            });
            codeToFutureMap.put(code, future);
        });
        checkFutrueException(codeToFutureMap);
        //数据库更新后 调用mysql函数 将mes表合到一起
//        finalUnfinishedDataMapper.callUpdateApsFinalUnfinishedData();
        return Boolean.TRUE;
    }

    private void checkFutrueException(Map<Integer, Future> codeToFutureMap) {
        List<Integer> errCode = new ArrayList<>();
        codeToFutureMap.forEach((k, v) -> {
            try {
                v.get();
            } catch (Exception e) {
                e.printStackTrace();
                errCode.add(k);
            }
        });
        if (errCode.size() != 0) {
            String errTableName = errCode.stream().map(x -> {
                InterfaceDataType interfaceDataType = InterfaceDataType.valueOfCode(x);
                return interfaceDataType != null ? interfaceDataType.getCnTableName() : "id:" + x;
            }).collect(Collectors.joining(","));
            throw new BeneWakeException(errTableName + "未更新成功 请联系管理员");
        }
    }

}
