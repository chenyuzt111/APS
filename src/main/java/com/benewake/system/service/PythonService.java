package com.benewake.system.service;

public interface PythonService  {
    void startScheduling() throws NoSuchFieldException, IllegalAccessException;

    void integrityChecker();
}
