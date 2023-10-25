package com.benewake.system.utils.python;

import com.benewake.system.entity.system.SysUser;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.utils.HostHolder;
import com.benewake.system.utils.threadpool.BenewakeExecutor;
import org.apache.commons.collections4.CollectionUtils;
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
                e.printStackTrace();
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
