package com.benewake.system.utils.python;


import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.ApsTableVersion;
import com.benewake.system.entity.SseMessageEntity;
import com.benewake.system.entity.enums.SchedulingResultType;
import com.benewake.system.entity.enums.TableVersionState;
import com.benewake.system.redis.DistributedLock;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.scheduling.message.SseService;
import com.benewake.system.service.scheduling.result.ApsProductionPlanService;
import com.benewake.system.service.scheduling.result.ApsSchedulingResuleBase;
import com.benewake.system.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.benewake.system.redis.SchedulingLockKey.SCHEDULING_DATA_LOCK_KEY;


@Component
public class SchedulingPythonService extends PythonBase {

    public SchedulingPythonService(@Value("${myPython.directory}") String directory, @Value("${myPython.startClass.scheduling}") String startClass) {
        super(directory, startClass);
    }

    @Autowired
    private SseService sseService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private ApsTableVersionService apsTableVersionService;

    @Autowired
    private DistributedLock distributedLock;

    @Autowired
    private ApsProductionPlanService apsProductionPlanService;

    @Autowired
    private Map<String , ApsSchedulingResuleBase> resuleBaseMap;

    @Override
    public void checkCode(String line) {
        System.out.println("----------arg：" + line);
        if ("521".equals(line)) {
            String username = hostHolder.getUser().getUsername();
            //通知前端成功
            //将最新版本的状态改为排程已完成
            Integer maxVersion = apsTableVersionService.getMaxVersion();
            LambdaUpdateWrapper<ApsTableVersion> apsTableVersionLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            apsTableVersionLambdaUpdateWrapper.set(ApsTableVersion::getState, TableVersionState.SUCCESS.getCode());
            apsTableVersionLambdaUpdateWrapper.eq(ApsTableVersion::getVersionNumber, maxVersion);
            apsTableVersionService.update(apsTableVersionLambdaUpdateWrapper);
            //更新结果表的版本号
            List<ApsTableVersion> apsTableVersions = new ArrayList<>();
            resuleBaseMap.forEach((k ,v) -> {
                //设置null值
                Integer maxVersionAndSave = v.getMaxVersionAndSave();
                int code = SchedulingResultType.getCodeByServiceName(k);
                ApsTableVersion apsTableVersion = ApsTableVersion.builder()
                        .tableId(code)
                        .tableVersion(maxVersionAndSave)
                        .versionNumber(maxVersion)
                        .state(TableVersionState.SUCCESS.getCode())
                        .versionTime(new Date())
                        .build();
                apsTableVersions.add(apsTableVersion);
            });
            //插入结果表的版本
            apsTableVersionService.saveBatch(apsTableVersions);
            distributedLock.releaseLock(SCHEDULING_DATA_LOCK_KEY, TableVersionState.SCHEDULING_ING.getDescription());
            sendMessage(username ,"排程已完成 快去查看吧~~" , "success");
        } else if ("520".equals(line)) {
            deleteVersionIsNull();
            String username = hostHolder.getUser().getUsername();
            setErrorState();
            distributedLock.releaseLock(SCHEDULING_DATA_LOCK_KEY, TableVersionState.SCHEDULING_ING.getDescription());
            sendMessage(username ,"排程失败了 联系一下管理员" , "error");
        }
    }

    private void sendMessage(String username, String message ,String state) {
        SseMessageEntity sseMessageEntity = new SseMessageEntity();
        sseMessageEntity.setMessage(message);
        sseMessageEntity.setDate(new Date());
        sseMessageEntity.setState(state);
        String toJSONString = JSON.toJSONString(sseMessageEntity);
        sseService.sendMessage(username, toJSONString);
    }

    private void deleteVersionIsNull() {
        resuleBaseMap.forEach((k , v) -> {
            IService iService = (IService) v;
            QueryWrapper queryWrapper = new QueryWrapper<>();
            queryWrapper.isNull("version");
            iService.remove(queryWrapper);
        });
    }

    @Override
    void callPythonException() {
        sendMessage(hostHolder.getUser().getUsername() ,"排程失败了 联系一下管理员~~" , "error");
        setErrorState();
        deleteVersionIsNull();
        distributedLock.releaseLock(SCHEDULING_DATA_LOCK_KEY, TableVersionState.SCHEDULING_ING.getDescription());
    }

    private void setErrorState() {
        Integer maxVersion = apsTableVersionService.getMaxVersion();
        LambdaUpdateWrapper<ApsTableVersion> apsTableVersionLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        apsTableVersionLambdaUpdateWrapper.set(ApsTableVersion::getState, TableVersionState.INVALID.getCode());
        apsTableVersionLambdaUpdateWrapper.eq(ApsTableVersion::getVersionNumber, maxVersion);
        apsTableVersionService.update(apsTableVersionLambdaUpdateWrapper);
    }
}
