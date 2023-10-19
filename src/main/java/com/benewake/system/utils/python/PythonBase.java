package com.benewake.system.utils.python;

import com.benewake.system.entity.system.SysUser;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.utils.HostHolder;
import com.benewake.system.utils.threadpool.BenewakeExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public abstract class PythonBase {

//    private final String MYPYTHON_PATH = "/usr/bin/python3";
    private final String MYPYTHON_PATH = "D:\\python\\python.exe";

    @Autowired
    private HostHolder hostHolder;

    private ProcessBuilder processBuilder;

    private PythonBase() {
    }

    public PythonBase(String directory, String startClass) {
        //获取当前路径
        Path pythonScript = Paths.get(directory, startClass);
        this.processBuilder = new ProcessBuilder(MYPYTHON_PATH, pythonScript.toString());
        this.processBuilder.directory(new File(directory));
    }

    public void start(List<String> command) {
        Process process = null;
        BufferedReader reader = null;
        try {
            process = this.processBuilder.start();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                checkCode(line);
                Thread.sleep(1000);
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                callPythonException();
            }
        } catch (Exception e) {
            if (!(e instanceof BeneWakeException)) {
                callPythonException();
            }
            e.printStackTrace();
            throw new BeneWakeException(e.getMessage());
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


    public void startAsync(SysUser user ,List<String> com) {
        BenewakeExecutor.execute(() -> {
            hostHolder.setUser(user);
            start(com);
        });
    }

    abstract void checkCode(String line);

    abstract void callPythonException();
}
