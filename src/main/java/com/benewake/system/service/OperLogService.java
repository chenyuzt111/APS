package com.benewake.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.system.SysOperLog;
import com.benewake.system.entity.vo.SysOperLogQueryVo;

import java.util.List;

public interface OperLogService extends IService<SysOperLog> {
    /**
     * 条件查询操作日志
     * @param sysOperLogQueryVo
     * @return
     */
    List<SysOperLog> selectOperLogs(SysOperLogQueryVo sysOperLogQueryVo);
}
