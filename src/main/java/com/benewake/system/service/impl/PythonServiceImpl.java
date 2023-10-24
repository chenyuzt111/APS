package com.benewake.system.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.benewake.system.entity.ApsTableVersion;
import com.benewake.system.entity.enums.InterfaceDataType;
import com.benewake.system.entity.enums.TableVersionState;
import com.benewake.system.entity.vo.SchedulingParam;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.redis.DistributedLock;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.ApsIntfaceDataServiceBase;
import com.benewake.system.service.PythonService;
import com.benewake.system.utils.HostHolder;
import com.benewake.system.utils.StringUtils;
import com.benewake.system.utils.python.PythonBase;
import com.google.gson.Gson;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.benewake.system.redis.SchedulingLockKey.SCHEDULING_DATA_LOCK_KEY;

@Service
public class PythonServiceImpl implements PythonService {

    @Autowired
    private HostHolder hostHolder;

    @Resource
    private PythonBase schedulingPythonService;

    @Resource
    private PythonBase integrityCheckerPythonService;

    @Autowired
    private ApsTableVersionService apsTableVersionService;

    @Autowired
    private List<ApsIntfaceDataServiceBase> apsIntfaceDataServiceBase;

    @Autowired
    private DistributedLock distributedLock;

    @Override
    public void startScheduling(SchedulingParam schedulingParam) {

        try {
            Integer schedulingMaxVersion = apsTableVersionService.getMaxVersion();
            ArrayList<ApsTableVersion> apsTableVersions = new ArrayList<>();
            for (ApsIntfaceDataServiceBase service : apsIntfaceDataServiceBase) {
                Integer maxVersion = service.getMaxVersionIncr();
                if (maxVersion == 1) {
                    throw new BeneWakeException("数据库数据不存在");
                }
                int codeByServiceName = getCodeByServiceName(service);
                ApsTableVersion apsTableVersion = ApsTableVersion.builder().tableVersion(maxVersion - 1)
                        .tableId(codeByServiceName)
                        .versionNumber(schedulingMaxVersion + 1)
                        .versionTime(new Date())
                        .state(TableVersionState.SCHEDULING_ING.getCode())
                        .build();
                apsTableVersions.add(apsTableVersion);
            }
            apsTableVersionService.saveBatch(apsTableVersions);
//            Gson gson = new Gson();
//            String json = gson    .toJson(schedulingParam);
            String jsonString = JSONObject.toJSONString(schedulingParam);
            jsonString = jsonString.replaceAll("\"", "\\\\\"");
            //todo 转json传参
            List<String> strings = new ArrayList<>();
            strings.add(jsonString);
            System.out.println(strings);
            schedulingPythonService.startAsync(hostHolder.getUser(), strings);
        }catch (Exception e){
            distributedLock.releaseLock(SCHEDULING_DATA_LOCK_KEY, TableVersionState.SCHEDULING_ING.getDescription());
            throw new BeneWakeException(e.getMessage());
        }
    }

    private int getCodeByServiceName(ApsIntfaceDataServiceBase service) {
        Class<?> aClass = AopProxyUtils.ultimateTargetClass(service);
        String simpleName = aClass.getSimpleName();
        return InterfaceDataType.getCodeByServiceName(StringUtils.toLowerCaseFirstLetter(simpleName));
    }

    @Override
    public void integrityChecker() {
        integrityCheckerPythonService.start(null);
    }

}
