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
    //表示python解释器的路径
    @Value("${myPython.path}")
    private String MYPYTHON_PATH;

    @Autowired
    private HostHolder hostHolder;

    //构建进程对象
    private ProcessBuilder processBuilder;

    //python脚本路径
    private String pythonScriptPath;

    private PythonBase() {
    }

//    有参构造方法，接收python脚本所在的目录和类名
    public PythonBase(String directory, String startClass) {
        processBuilder = new ProcessBuilder();
        //获取当前路径
        Path pythonScript = Paths.get(directory, startClass);
        //设置解释器目录和python脚本路径
        this.pythonScriptPath = pythonScript.toString();
        this.processBuilder.directory(new File(directory));
    }

    public void start(List<String> arg) {
        //声明进程和缓冲读取器
        Process process = null;
        BufferedReader reader = null;
        //创建一个字符串列表，存储执行脚本命令
        ArrayList<String> command = new ArrayList<>();
        //分别添加解释器路径和python脚本路径命令
        command.add(MYPYTHON_PATH);
        command.add(pythonScriptPath);
        //接收到的指令加入
        if (CollectionUtils.isNotEmpty(arg)) {
            command.addAll(arg);
        }
        try {
            //使用创建好的命令启动进程
            process = this.processBuilder.command(command).start();
            //创建一个读取进程输出流的读取器
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

//             创建线程捕获并输出标准错误输出
            Process finalProcess = process;
//            创建一个新的线程异步处理标准错误输出
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
//            启动标准错误输出线程
            stdErrThread.start();
//          在主线程中等待stdErrThread执行完毕，确保在主线程执行之前，标准错误输出线程已完成
            stdErrThread.join();
            String line;
            while ((line = reader.readLine()) != null) {
                checkCode(line);
                Thread.sleep(1000);
            }
//            等待进程结束获取退出码
            int exitCode = process.waitFor();
//            如果退出码不为0，记录错误信息，并调用callPythonException处理异常信息
            if (exitCode != 0) {
                log.error("path:=--" + pythonScriptPath + "exitCode Python程序退出" + exitCode);
                callPythonException();
            }
        } catch (Exception e) {
            if (!(e instanceof BeneWakeException)) {
                log.error("异步操作python报错" + e.getMessage());
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
