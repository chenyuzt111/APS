package com.benewake.system.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.DateUtils;
import com.benewake.system.entity.ApsHolidayTable;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.vo.ApsAttendanceParam;
import com.benewake.system.entity.vo.ApsAttendanceVo;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.excel.entity.AttendanceTemplate;
import com.benewake.system.excel.entity.ExcelProcessCapacityTemplate;
import com.benewake.system.service.ApsAttendanceService;
import com.benewake.system.service.ApsHolidayTableService;
import com.benewake.system.utils.ResponseUtil;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Slf4j
@Api(tags = "出勤管理")
@RestController
@RequestMapping("/attendance")
public class AttendanceController {


    @Autowired
    private ApsAttendanceService apsAttendanceService;

    @Autowired
    private ApsHolidayTableService holidayTableService;

//    @ApiOperation("获取出勤管理")
//    @GetMapping("/list/{page}/{size}")
//    public Result getAttendanceManList(@PathVariable("page") Integer page, @PathVariable("size") Integer size) {
//        PageResultVo<ApsAttendanceVo> pageResultVo = apsAttendanceService.getAttendanceManList(page, size);
//        return Result.ok(pageResultVo);
//    }

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

    @ApiOperation("导入出勤管理")
    @PostMapping("/importAttendance")
    public Result importAttendance(@PathParam("type") Integer type, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("文件为空！");
        }
        String[] split = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
        if (!"xlsx".equals(split[1]) && !"xls".equals(split[1])) {
            return Result.fail("请提供.xlsx或.xls为后缀的Excel文件");
        }
        Boolean res = apsAttendanceService.saveDataByExcel(type, file);
        return res ? Result.ok() : Result.fail();
    }

    @ApiOperation("下载工序与产能导入模板")
    @PostMapping("/attendanceTemplate")
    public void attendanceTemplate(HttpServletResponse response) {
        try {
            ResponseUtil.setFileResp(response, "出勤管理导入模板");
            EasyExcel.write(response.getOutputStream(), AttendanceTemplate.class).sheet("sheet1")
                    .doWrite((java.util.Collection<?>) null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @ApiOperation("刷新节假日表")
    @GetMapping("/updateHoliday")
    public Result updateHoliday() {
        holidayTableService.updateHoliday();
        return Result.ok();
    }

}
