package com.benewake.system.utils.python;


import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.benewake.system.entity.ApsProductionPlan;
import com.benewake.system.entity.ApsTableVersion;
import com.benewake.system.entity.SseMessageEntity;
import com.benewake.system.entity.enums.TableVersionState;
import com.benewake.system.redis.DistributedLock;
import com.benewake.system.service.ApsProductionPlanService;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.message.SseService;
import com.benewake.system.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

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

    @Override
    public void checkCode(String line) {
        // todo 将结果(version in null)加上版本号 并且添加表控制的数据 并根据结果设置他的状态 为失败还是成功
        // todo 如果是试一试并且想查看结果的话 那么应该保存不同的状态 在删除的时候删除掉而不是占用已有的名额 这需要在查询的时候做判断
        System.out.println("arg1111" + line);
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
            LambdaQueryWrapper<ApsProductionPlan> apsProductionPlanLambdaQueryWrapper = new LambdaQueryWrapper<>();
            apsProductionPlanLambdaQueryWrapper
                    .orderByDesc(ApsProductionPlan::getVersion)
                    .last("limit 1")
                    .select(ApsProductionPlan::getVersion);
            ApsProductionPlan one = apsProductionPlanService.getOne(apsProductionPlanLambdaQueryWrapper);
            Integer curVersion = 0;
            if (one == null || one.getVersion() == null) {
                curVersion = 1;
            } else {
                curVersion = one.getVersion() + 1;
            }
            LambdaUpdateWrapper<ApsProductionPlan> apsProductionPlanLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            apsProductionPlanLambdaUpdateWrapper.set(ApsProductionPlan::getVersion, curVersion).isNull(ApsProductionPlan::getVersion);
            apsProductionPlanService.update(apsProductionPlanLambdaUpdateWrapper);
            //todo 结果表的版本号 保存到对应关系中 23 成品表
            ApsTableVersion apsTableVersion = ApsTableVersion.builder()
                    .tableId(23)
                    .tableVersion(curVersion)
                    .versionNumber(maxVersion)
                    .state(TableVersionState.SUCCESS.getCode())
                    .versionTime(new Date())
                    .build();
            apsTableVersionService.save(apsTableVersion);
            distributedLock.releaseLock(SCHEDULING_DATA_LOCK_KEY, TableVersionState.SCHEDULING_ING.getDescription());
            sendMessage(username ,"排程已完成 快去查看吧~~" , "success");
//            sseService.sendMessage(username, "排程已完成 快去查看吧~~");
        } else if ("520".equals(line)) {
            deleteVersionIsNull();
            String username = hostHolder.getUser().getUsername();
            distributedLock.releaseLock(SCHEDULING_DATA_LOCK_KEY, TableVersionState.SCHEDULING_ING.getDescription());
            sendMessage(username ,"排程失败了 联系一下管理员" , "error");
//            sseService.sendMessage(username, "排程失败了 联系一下管理员");
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
        LambdaQueryWrapper<ApsProductionPlan> apsProductionPlanLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsProductionPlanLambdaQueryWrapper.isNull(ApsProductionPlan::getVersion);
        apsProductionPlanService.remove(apsProductionPlanLambdaQueryWrapper);
    }

    @Override
    void callPythonException() {
        sendMessage(hostHolder.getUser().getUsername() ,"排程失败了 联系一下管理员~~" , "error");
//        sseService.sendMessage(hostHolder.getUser().getUsername(), "排程失败了 联系一下管理员~~");
        Integer maxVersion = apsTableVersionService.getMaxVersion();
        LambdaUpdateWrapper<ApsTableVersion> apsTableVersionLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        apsTableVersionLambdaUpdateWrapper.set(ApsTableVersion::getState, TableVersionState.INVALID.getCode());
        apsTableVersionLambdaUpdateWrapper.eq(ApsTableVersion::getVersionNumber, maxVersion);
        apsTableVersionService.update(apsTableVersionLambdaUpdateWrapper);
        deleteVersionIsNull();
        distributedLock.releaseLock(SCHEDULING_DATA_LOCK_KEY, TableVersionState.SCHEDULING_ING.getDescription());
    }
}
