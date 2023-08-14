package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.system.SysLoginLog;
import com.benewake.system.entity.vo.SysLoginLogQueryVo;
import com.benewake.system.mapper.LoginLogMapper;
import com.benewake.system.service.LoginLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lcs
 * @since 2023年08月04 14:42
 * 描 述： TODO
 */
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, SysLoginLog> implements LoginLogService {
    @Override
    public void recordLoginLog(String username, Integer status,
                               String ipaddr, String message) {
        SysLoginLog sysLoginLog = new SysLoginLog();
        sysLoginLog.setUsername(username);
        sysLoginLog.setMsg(message);
        sysLoginLog.setIpaddr(ipaddr);
        sysLoginLog.setStatus(status);
        baseMapper.insert(sysLoginLog);
    }

    @Override
    public List<SysLoginLog> selectLoginLogs(SysLoginLogQueryVo sysLoginLogQueryVo) {
        LambdaQueryWrapper<SysLoginLog> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(sysLoginLogQueryVo.getUsername()),
                SysLoginLog::getUsername,sysLoginLogQueryVo.getUsername())
                .ge(StringUtils.isNotEmpty(sysLoginLogQueryVo.getCreateTimeBegin()),
                        SysLoginLog::getCreateTime,sysLoginLogQueryVo.getCreateTimeBegin())
                .le(StringUtils.isNotEmpty(sysLoginLogQueryVo.getCreateTimeEnd()),
                        SysLoginLog::getCreateTime,sysLoginLogQueryVo.getCreateTimeEnd());
        return baseMapper.selectList(lqw);
    }
}
