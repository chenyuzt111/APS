package com.benewake.system.KindDee;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.ApsSystemApplication;
import com.benewake.system.entity.ApsTableVersion;
import com.benewake.system.entity.enums.InterfaceDataType;
import com.benewake.system.entity.enums.TableVersionState;
import com.benewake.system.entity.system.SysUser;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsIntfaceDataServiceBase;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.utils.HostHolder;
import com.benewake.system.utils.threadpool.BenewakeExecutor;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.benewake.system.redis.SchedulingLockKey.SCHEDULING_USER_LOCK_KEY;

@SpringBootTest
@ContextConfiguration(classes = ApsSystemApplication.class)
public class PythonBase {

    private final String MYPYTHON_PATH = "D:\\python\\python.exe";


    private ProcessBuilder processBuilder;

    private String pythonScriptPath;

    @Autowired
    private Map<String, ApsIntfaceDataServiceBase> kingdeeServiceMap;

    @Autowired
    private ApsTableVersionService apsTableVersionService;

    @Autowired
    private RedissonClient redisson;
    @Test
    public void test1() throws InterruptedException {
        RLock fairLock = redisson.getFairLock("123");
        System.out.println(fairLock.tryLock(10 ,TimeUnit.SECONDS));
        System.out.println(fairLock.tryLock(10 ,TimeUnit.SECONDS));
        Thread.sleep(20000);
    }

    @Test
    public void test() {
        Set<Map.Entry<String, ApsIntfaceDataServiceBase>> entries = kingdeeServiceMap.entrySet();
        for (Map.Entry<String, ApsIntfaceDataServiceBase> entry : entries) {
            String k = entry.getKey();
            ApsIntfaceDataServiceBase value = entry.getValue();
            int code = InterfaceDataType.getCodeByServiceName(k);
            LambdaQueryWrapper<ApsTableVersion> apsTableVersionLambdaQueryWrapper = new LambdaQueryWrapper<>();
            apsTableVersionLambdaQueryWrapper.eq(ApsTableVersion::getTableId, code)
                    .eq(ApsTableVersion::getState, TableVersionState.SUCCESS.getCode())
                    .orderByDesc(ApsTableVersion::getVersionNumber)
                    .last(" limit 5");
            List<ApsTableVersion> apsTableVersions = apsTableVersionService.getBaseMapper().selectList(apsTableVersionLambdaQueryWrapper);
            if (CollectionUtils.isEmpty(apsTableVersions)) {
                continue;
            }
            List<Integer> tableVersion = apsTableVersions.stream().map(ApsTableVersion::getTableVersion).distinct().collect(Collectors.toList());
            IService iService = (IService) value;
            QueryWrapper<Object> objectQueryWrapper = new QueryWrapper<>();
            objectQueryWrapper.notIn("version", tableVersion);
            boolean remove = iService.remove(objectQueryWrapper);
            System.err.println(remove);
        }
    }

    @Test
    public void testDa1ta() {
        processBuilder = new ProcessBuilder();
        //获取当前路径
        Path pythonScript = Paths.get("E:\\桌面\\APS_For_Benwake_process_kaolv\\src", "config.py");
        this.pythonScriptPath = pythonScript.toString();
        this.processBuilder.directory(new File("E:\\桌面\\APS_For_Benwake_process_kaolv\\src"));
        ArrayList<String> strings = new ArrayList<>();
        strings.add("{\n" +
                "                \"split_po_orders\": true,\n" +
                "                \"consider_the_material\": true,\n" +
                "                \"start_time\": \"2023-10-19\"\n" +
                "                }");
        start(strings);
    }

    public void start(List<String> arg) {
        Process process = null;
        BufferedReader reader = null;
        ArrayList<String> command = new ArrayList<>();
        command.add(MYPYTHON_PATH);
        command.add(pythonScriptPath);
        if (CollectionUtils.isNotEmpty(arg)) {
            command.addAll(arg);
        }
        try {
            process = this.processBuilder.command(command).start();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();

        } catch (Exception e) {

        } finally {

        }
    }

}
