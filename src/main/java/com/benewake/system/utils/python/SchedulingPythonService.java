package com.benewake.system.utils.python;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.benewake.system.entity.ApsTableVersion;
import com.benewake.system.entity.enums.TableVersionState;
import com.benewake.system.redis.DistributedLock;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.message.SseService;
import com.benewake.system.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    @Override
    void checkCode(String line) {
        // todo 将结果(version in null)加上版本号 并且添加表控制的数据 并根据结果设置他的状态 为失败还是成功
        // todo 如果是试一试并且想查看结果的话 那么应该保存不同的状态 在删除的时候删除掉而不是占用已有的名额 这需要在查询的时候做判断
        System.out.println("arg1111" + line);
        if ("521".equals(line)) {
            String username = hostHolder.getUser().getUsername();
            //通知前端成功
            //将最新版本的状态改为排程已完成
            Integer maxVersion = apsTableVersionService.getMaxVersion();
            LambdaUpdateWrapper<ApsTableVersion> apsTableVersionLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            apsTableVersionLambdaUpdateWrapper.set(ApsTableVersion::getState, TableVersionState.SCHEDULING.getCode());
            apsTableVersionLambdaUpdateWrapper.eq(ApsTableVersion::getVersionNumber, maxVersion);
            apsTableVersionService.update(apsTableVersionLambdaUpdateWrapper);
            distributedLock.releaseLock(SCHEDULING_DATA_LOCK_KEY, TableVersionState.SCHEDULING_ING.getDescription());
            sseService.sendMessage(username, "排程已完成 快去查看吧~~");
        } else if ("520".equals(line)) {
            String username = hostHolder.getUser().getUsername();
            distributedLock.releaseLock(SCHEDULING_DATA_LOCK_KEY, TableVersionState.SCHEDULING_ING.getDescription());
            sseService.sendMessage(username, "排程失败了 联系一下管理员~~");
        }
    }

    @Override
    void callPythonException() {
        sseService.sendMessage(hostHolder.getUser().getUsername(), "排程失败了 联系一下管理员~~");
        Integer maxVersion = apsTableVersionService.getMaxVersion();
        LambdaUpdateWrapper<ApsTableVersion> apsTableVersionLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        apsTableVersionLambdaUpdateWrapper.set(ApsTableVersion::getState, TableVersionState.INVALID.getCode());
        apsTableVersionLambdaUpdateWrapper.eq(ApsTableVersion::getVersionNumber, maxVersion);
        apsTableVersionService.update(apsTableVersionLambdaUpdateWrapper);
    }
}
