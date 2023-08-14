package com.benewake.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.system.SysLoginLog;
import com.benewake.system.entity.vo.SysLoginLogQueryVo;

import java.util.List;

/**
 * @author Lcs
 * @since 2023年08月04 14:41
 * 描 述： TODO
 */
public interface LoginLogService extends IService<SysLoginLog> {
    public void recordLoginLog(String username,Integer status,
                               String ipaddr,String message);

    /**
     * 条件查询登录日志
     * @param sysLoginLogQueryVo
     * @return
     */
    List<SysLoginLog> selectLoginLogs(SysLoginLogQueryVo sysLoginLogQueryVo);
}
