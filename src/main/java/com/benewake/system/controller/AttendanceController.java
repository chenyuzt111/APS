package com.benewake.system.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.util.DateUtils;
import com.benewake.system.entity.ApsHolidayTable;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.vo.ApsAttendanceParam;
import com.benewake.system.entity.vo.ApsAttendanceVo;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.service.ApsAttendanceService;
import com.benewake.system.service.ApsHolidayTableService;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {


    @Autowired
    private ApsAttendanceService apsAttendanceService;

    @Autowired
    private ApsHolidayTableService holidayTableService;

    @ApiOperation("获取出勤管理")
    @GetMapping("/list/{page}/{size}")
    public Result getAttendanceManList(@PathVariable("page") Integer page, @PathVariable("size") Integer size) {
        PageResultVo<ApsAttendanceVo> pageResultVo = apsAttendanceService.getAttendanceManList(page, size);
        return Result.ok(pageResultVo);
    }

    @ApiOperation("添加或更新出勤管理")
    @PostMapping("/addOrUpdate")
    public Result addOrUpdateAttendance(@RequestBody ApsAttendanceParam attendanceParam) {
        if (attendanceParam == null || attendanceParam.getDate() == null) {
            return Result.fail("参数不能为null");
        }
        boolean res = apsAttendanceService.addOrUpdateAttendance(attendanceParam);
        return res ? Result.ok() : Result.fail();
    }

    @ApiOperation("删除出勤管理")
    @PostMapping("/remove")
    public Result removeAttendance(@RequestBody List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Result.fail("参数不能为null");
        }
        boolean res = apsAttendanceService.removeBatchByIds(ids);
        return res ? Result.ok() : Result.fail();
    }


    @ApiOperation("刷新节假日表")
    @GetMapping("/updateHoliday")
    public Result updateHoliday() {
        holidayTableService.updateHoliday();
        return Result.ok();
    }

}
