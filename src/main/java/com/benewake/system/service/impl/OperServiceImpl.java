package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.system.SysOperLog;
import com.benewake.system.entity.vo.SysOperLogQueryVo;
import com.benewake.system.mapper.OperLogMapper;
import com.benewake.system.service.OperLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lcs
 * @since 2023年08月04 15:13
 * 描 述： TODO
 */
@Service
public class OperServiceImpl extends ServiceImpl<OperLogMapper, SysOperLog> implements OperLogService {

    @Override
    public List<SysOperLog> selectOperLogs(SysOperLogQueryVo sysOperLogQueryVo) {
        LambdaQueryWrapper<SysOperLog> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(sysOperLogQueryVo.getTitle()),
                        SysOperLog::getTitle,sysOperLogQueryVo.getTitle())
                .like(StringUtils.isNotEmpty(sysOperLogQueryVo.getOperName()),
                        SysOperLog::getOperName,sysOperLogQueryVo.getOperName())
                .ge(StringUtils.isNotEmpty(sysOperLogQueryVo.getCreateTimeBegin()),
                        SysOperLog::getCreateTime,sysOperLogQueryVo.getCreateTimeBegin())
                .le(StringUtils.isNotEmpty(sysOperLogQueryVo.getCreateTimeEnd()),
                        SysOperLog::getCreateTime,sysOperLogQueryVo.getCreateTimeEnd());
        return baseMapper.selectList(lqw);
    }
}
