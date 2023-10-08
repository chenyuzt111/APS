package com.benewake.system.KindDee;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JavaCallPythonDemo {
    public static void main(String[] args) {
        try {
            // Python脚本的路径
            String pythonScriptPath = "E:\\桌面\\my_script.py";

            // 传递给Python脚本的参数
            String[] pythonScriptArguments = {"arg1", "arg2", "arg3"};

            // 创建ProcessBuilder来运行Python脚本
            ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath);
            
            // 设置工作目录（如果需要的话）
            // pb.directory(new File("path/to/your/script/directory"));

            // 向Python脚本传递参数
            pb.command().addAll(java.util.Arrays.asList(pythonScriptArguments));

            Process process = pb.start();

            // 从Python进程的输出中读取数据
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 等待Python进程完成
            int exitCode = process.waitFor();
            System.out.println("Python脚本执行完毕，退出码：" + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}