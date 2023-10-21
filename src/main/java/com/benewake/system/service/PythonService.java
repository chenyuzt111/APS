package com.benewake.system.service;

import com.benewake.system.entity.vo.SchedulingParam;

public interface PythonService  {
    void startScheduling(SchedulingParam schedulingParam);

    void integrityChecker();
}
