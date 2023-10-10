package com.benewake.system.utils.python;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SchedulingPythonService extends PythonBase {
    public SchedulingPythonService(@Value("${myPython.directory}") String directory, @Value("${myPython.startClass.scheduling}") String startClass) {
        super(directory, startClass);
    }

    @Override
    void checkCode(String line) {
        if ("521".equals(line)) {
            //通知前端成功
            System.out.println("通知前端排程成功-------------------------");
        } else if ("520".equals(line)) {
            System.out.println("通知前端排程失败-------------------------");
        }
    }

    @Override
    void callPythonException() {
        System.out.println("通知前端程序异常退出--------");
    }
}
