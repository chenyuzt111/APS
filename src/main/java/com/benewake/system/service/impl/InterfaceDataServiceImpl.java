package com.benewake.system.service.impl;

import com.benewake.system.entity.enums.InterfaceDataType;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.InterfaceDataService;
import com.benewake.system.service.KingdeeService;
import com.sun.jmx.snmp.Timestamp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class InterfaceDataServiceImpl implements InterfaceDataService {

    @Autowired
    private Map<String, KingdeeService> kingdeeServiceMap;

    @Autowired
    private ApsTableVersionService apsTableVersionService;

    @Override
    public Boolean updateData(List<Integer> ids) throws Exception {
        for (Integer code : ids) {
            String serviceName = InterfaceDataType.serviceNameOfCode(code);
            if (StringUtils.isBlank(serviceName)) {
                throw new BeneWakeException("id为：" + code + "未找到对应service"); // 如果 serviceName 为空，跳过当前循环
            }

            KingdeeService service = kingdeeServiceMap.get(serviceName);
            if (service == null) {
                throw new BeneWakeException(serviceName + "未找到");// 如果未找到对应的服务，跳过当前循环
            }
            apsTableVersionService.incrVersions(code);
            Boolean res = service.updateDataVersions();
            if (!res) {
                throw new BeneWakeException(serviceName + "数据库更新异常");
            }
        }
        return Boolean.TRUE;
    }
}
