package com.benewake.system.controller;

import com.benewake.system.entity.Result;
import com.benewake.system.entity.system.SysLoginLog;
import com.benewake.system.entity.system.SysOperLog;
import com.benewake.system.entity.vo.SysLoginLogQueryVo;
import com.benewake.system.entity.vo.SysOperLogQueryVo;
import com.benewake.system.service.LoginLogService;
import com.benewake.system.service.OperLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Lcs
 * @since 2023年08月04 15:30
 * 描 述： TODO
 */
@Api(value = "SysLog管理",tags = "SysLog管理")
@RestController
@RequestMapping(value = "/admin/system/sysLog")
public class SysLogController {

    @Autowired
    private LoginLogService loginLogService;
    @Autowired
    private OperLogService operLogService;
    @PreAuthorize("hasAnyAuthority('bnt.sysLoginLog.list')")
    @ApiOperation("条件查询登录日志")
    @PostMapping("loginLog")
    public Result findLoginLogs(@RequestBody SysLoginLogQueryVo sysLoginLogQueryVo){
        List<SysLoginLog> loginLogs = loginLogService.selectLoginLogs(sysLoginLogQueryVo);
        return Result.ok(loginLogs);
    }

    @PreAuthorize("hasAnyAuthority('bnt.sysOperLog.list')")
    @ApiOperation("条件查询操作日志")
    @PostMapping("operLog")
    public Result findOperLogs(@RequestBody SysOperLogQueryVo sysOperLogQueryVo){
        List<SysOperLog> operLogs = operLogService.selectOperLogs(sysOperLogQueryVo);
        return Result.ok(operLogs);
    }


}
