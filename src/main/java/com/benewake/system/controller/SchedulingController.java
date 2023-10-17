package com.benewake.system.controller;

import com.benewake.system.annotation.Scheduling;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.enums.InterfaceDataType;
import com.benewake.system.entity.enums.TableVersionState;
import com.benewake.system.entity.vo.ReturnTest;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.redis.DistributedLock;
import com.benewake.system.service.ApsAllPlanNumInProcessService;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.InterfaceDataService;
import com.benewake.system.service.PythonService;
import com.benewake.system.utils.HostHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.benewake.system.redis.SchedulingLockKey.SCHEDULING_USER_LOCK_KEY;

/**
 * 接口数据
 *
 * @author wangxuyue
 * @since 2023-09-27
 */
@Api(tags = "排程")
@RestController
@RequestMapping("/scheduling")
public class SchedulingController {

    @Autowired
    private InterfaceDataService interfaceDataService;

    @Autowired
    private PythonService pythonService;

    @Autowired
    private ApsAllPlanNumInProcessService apsAllPlanNumInProcessService;

    @Autowired
    private DistributedLock distributedLock;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @ApiOperation("数据库更新")
    @Scheduling(type = TableVersionState.UPDATE_DATABASE_ING)
    @PostMapping("/dataUpdate")
    public Result dataUpdate(@RequestBody List<Integer> ids) throws Exception {
        long l = System.currentTimeMillis();
        if (CollectionUtils.isEmpty(ids)) {
            ids = InterfaceDataType.getAllIds();
        }

        interfaceDataService.updateData(ids);
        long l1 = System.currentTimeMillis();
        System.err.println(l1 - l);
        return Result.ok();
    }


    @ApiOperation("开始排程")
    @Scheduling(type = TableVersionState.SCHEDULING_ING)
    @PostMapping("/startScheduling")
    public Result startScheduling() {
        pythonService.startScheduling();
        return Result.ok();
    }

    @ApiOperation("完整性检查")
    @Scheduling(type = TableVersionState.INTEGRIT_CHECKER_ING)
    @PostMapping("/integrityChecker")
    public Result integrityChecker() {
        pythonService.integrityChecker();
        return Result.ok();
    }

    @ApiOperation("测试")
    @PostMapping("/getAllPlanNumInProcess")
    public Result<ReturnTest> all_plan_num_in_process() {
        ReturnTest latestCompletion = apsAllPlanNumInProcessService.getLatestCompletion();
        return Result.ok(latestCompletion);
    }


    @ApiOperation("获取排程界面的所有权")
    @PostMapping("/getPageLock")
    public Result getPageLock() {
        if (distributedLock.acquireLock(SCHEDULING_USER_LOCK_KEY, hostHolder.getUser().getUsername())) {
            return Result.ok();
        } else {
            String username = redisTemplate.opsForValue().get(SCHEDULING_USER_LOCK_KEY);
            if(Objects.equals(username, hostHolder.getUser().getUsername())) {
                distributedLock.startLockRenewalTask(SCHEDULING_USER_LOCK_KEY);
                return Result.ok();
            }
            return Result.fail(username + "正在使用");
        }
    }

    @ApiOperation("锁续期")
    @PostMapping("/lockRenewal")
    public Result lockRenewal() {
        String username = hostHolder.getUser().getUsername();
        String redisUsername = redisTemplate.opsForValue().get(SCHEDULING_USER_LOCK_KEY);
        if (username.equals(redisUsername)) {
            distributedLock.startLockRenewalTask(SCHEDULING_USER_LOCK_KEY);
            return Result.ok();
        }
        return Result.fail();
    }

    @ApiOperation("关闭锁")
    @PostMapping("/closeLock")
    public Result closeLock() {
        String username = hostHolder.getUser().getUsername();
        String redisUsername = redisTemplate.opsForValue().get(SCHEDULING_USER_LOCK_KEY);
        if (username.equals(redisUsername)) {
            distributedLock.releaseLock(SCHEDULING_USER_LOCK_KEY ,username);
            return Result.ok();
        }
        return Result.fail();
    }

}
