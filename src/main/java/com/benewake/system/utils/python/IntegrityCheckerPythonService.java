package com.benewake.system.utils.python;

import com.benewake.system.exception.BeneWakeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class IntegrityCheckerPythonService extends PythonBase {
    public IntegrityCheckerPythonService(@Value("${myPython.integrityCheckerDirectory}") String directory, @Value("${myPython.startClass.integrityChecker}")String startClass) {
        super(directory, startClass);
    }

    @Override
    void checkCode(String line) {
       if ("0".equals(line)) {
            throw new BeneWakeException("完整性检查失败！");
        }
    }

    @Override
    void callPythonException() {
        throw new BeneWakeException("完整性检查失败！-----内部服务器问题");
    }
}
