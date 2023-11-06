package com.benewake.system.utils.python;

import com.benewake.system.entity.system.SysUser;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.utils.HostHolder;
import com.benewake.system.utils.threadpool.BenewakeExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public abstract class PythonBase {

    //    private final String MYPYTHON_PATH = "/usr/bin/python3";
    @Value("${myPython.path}")
    private String MYPYTHON_PATH;

    @Autowired
    private HostHolder hostHolder;

    private ProcessBuilder processBuilder;

    private String pythonScriptPath;

    private PythonBase() {
    }

    public PythonBase(String directory, String startClass) {
        processBuilder = new ProcessBuilder();
        //获取当前路径
        Path pythonScript = Paths.get(directory, startClass);
        this.pythonScriptPath = pythonScript.toString();
        this.processBuilder.directory(new File(directory));
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

//             创建线程捕获并输出标准错误输出
            Process finalProcess = process;
            Thread stdErrThread = new Thread(() -> {
                try (BufferedReader readera = new BufferedReader(new InputStreamReader(finalProcess.getErrorStream()))) {
                    String line;
                    while ((line = readera.readLine()) != null) {
                        System.err.println("Standard Error Output: " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            stdErrThread.start();
            stdErrThread.join();
            String line;
            while ((line = reader.readLine()) != null) {
                checkCode(line);
                Thread.sleep(1000);
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("path:=--" + pythonScriptPath + "exitCode Python程序退出" + exitCode);
                callPythonException();
            }
        } catch (Exception e) {
            System.out.println(processBuilder);
            System.out.println("抛异常了");
            if (!(e instanceof BeneWakeException)) {
                e.printStackTrace();
                log.error("异步操作python报错" + e);
                callPythonException();
            }
            e.printStackTrace();
            assert e instanceof BeneWakeException;
            throw new BeneWakeException(((BeneWakeException) e).getCode(), e.getMessage());
        } finally {
            // 在finally块中关闭资源
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy(); // 终止进程
            }
        }
    }


    public void startAsync(SysUser user, List<String> com) {
        BenewakeExecutor.execute(() -> {
            hostHolder.setUser(user);
            start(com);
        });
    }

    abstract void checkCode(String line);

    abstract void callPythonException();
}
