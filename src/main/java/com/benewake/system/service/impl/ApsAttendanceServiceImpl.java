package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsAttendance;
import com.benewake.system.entity.ApsHolidayTable;
import com.benewake.system.entity.ApsTimeSheet;
import com.benewake.system.entity.dto.ApsAttendanceDto;
import com.benewake.system.entity.vo.ApsAttendanceParam;
import com.benewake.system.entity.vo.ApsAttendanceVo;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsTimeSheetMapper;
import com.benewake.system.service.ApsAttendanceService;
import com.benewake.system.mapper.ApsAttendanceMapper;
import com.benewake.system.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ASUS
 * @description 针对表【aps_attendance】的数据库操作Service实现
 * @createDate 2023-12-05 15:56:08
 */
@Service
public class ApsAttendanceServiceImpl extends ServiceImpl<ApsAttendanceMapper, ApsAttendance>
        implements ApsAttendanceService {

    @Autowired
    private ApsAttendanceMapper apsAttendanceMapper;

    @Autowired
    private ApsTimeSheetMapper timeSheetMapper;

    @Autowired
    private ApsHolidayTableServiceImpl apsHolidayTableService;

    @PostConstruct
    public void init() {
        apsHolidayTableService.updateHoliday();
    }

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static Map<String, ApsHolidayTable> dateToHolidayMap;

    public static Date maxHolidayDate;


    @Override
    public PageResultVo<ApsAttendanceVo> getAttendanceManList(Integer page, Integer size) {
        Page<ApsAttendanceDto> apsAttendanceVoPage = new Page<>(page, size);
        Page<ApsAttendanceDto> attendanceVoPage = apsAttendanceMapper.getAttendancePage(apsAttendanceVoPage);
        return buildResultVo(attendanceVoPage);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addOrUpdateAttendance(ApsAttendanceParam attendanceParam) {
        ApsTimeSheet timeSheet = timeSheetMapper.selectOne(new LambdaQueryWrapper<ApsTimeSheet>().last("limit 1"));
        List<String> time;
        if (attendanceParam.getId() != null && !gettimeSheetUpdate(timeSheet, attendanceParam)) {
            //出勤时间修改了 重新计算所有的有效出勤时间
            ApsTimeSheet apsTimeSheet = buildTimeSheet(attendanceParam);
            timeSheetMapper.delete(null);
            timeSheetMapper.insert(apsTimeSheet);
            List<ApsAttendance> apsAttendances = list();
            apsAttendances = apsAttendances.stream().peek(x -> x.setEffectiveAttendanceTimeRange(String.join(",",
                    getEffectiveAttendanceTime(apsTimeSheet, x.getLeaveTimeRange())))).collect(Collectors.toList());
            updateBatchById(apsAttendances);
            time = getEffectiveAttendanceTime(apsTimeSheet, attendanceParam.getLeaveTimeRange());
        } else {
            //出勤时间没有修改 直接更新有效出勤时间
            time = getEffectiveAttendanceTime(timeSheet, attendanceParam.getLeaveTimeRange());
        }

        Date date = attendanceParam.getDate();
        // 检查日期大小
        if (date != null && date.after(maxHolidayDate)) {
            // 抛出异常或执行其他操作
            throw new BeneWakeException("日期超过最大节假日日期");
        }
        ApsAttendance apsAttendance = buildAttendancePo(attendanceParam, time);
        return apsAttendance.getId() == null ? save(apsAttendance) : updateById(apsAttendance);
    }

    private List<String> getEffectiveAttendanceTime(ApsTimeSheet timeSheet, String leaveTimeRange) {
        List<String> timeRanges = new ArrayList<>();
        String lunchBreakTimeRange = timeSheet.getLunchBreakTimeRange();
        if (StringUtils.isNotEmpty(lunchBreakTimeRange)) {
            timeRanges.add(lunchBreakTimeRange);
        }
        String dinnerTimeRange = timeSheet.getDinnerTimeRange();
        if (StringUtils.isNotEmpty(dinnerTimeRange)) {
            timeRanges.add(dinnerTimeRange);
        }
        String morningMeetingTimeRange = timeSheet.getMorningMeetingTimeRange();
        if (StringUtils.isNotEmpty(morningMeetingTimeRange)) {
            timeRanges.add(morningMeetingTimeRange);
        }
        if (StringUtils.isNotEmpty(leaveTimeRange)) {
            String[] split = leaveTimeRange.split(",");
            Collections.addAll(timeRanges, split);
        }
        String attendanceTimeRange = timeSheet.getAttendanceTimeRange();
        if (StringUtils.isEmpty(attendanceTimeRange)) {
            throw new BeneWakeException("没有默认出勤时间");
        }
        return DateUtils.subtractTimeRanges(new DateUtils.TimeRange(attendanceTimeRange), timeRanges);
    }

    private boolean gettimeSheetUpdate(ApsTimeSheet timeSheet, ApsAttendanceParam attendanceParam) {
        if (timeSheet == null || attendanceParam == null) {
            return false;
        }
        String attendanceTimeRange = timeSheet.getAttendanceTimeRange();
        String dinnerTimeRange = timeSheet.getDinnerTimeRange();
        String lunchBreakTimeRange = timeSheet.getLunchBreakTimeRange();
        String morningMeetingTimeRange = timeSheet.getMorningMeetingTimeRange();
        if (attendanceTimeRange.equals(attendanceParam.getAttendanceTimeRange())
                && dinnerTimeRange.equals(attendanceParam.getDinnerTimeRange())
                && lunchBreakTimeRange.equals(attendanceParam.getLunchBreakTimeRange())
                && morningMeetingTimeRange.equals(attendanceParam.getMorningMeetingTimeRange())) {
            return true;
        }
        return false;
    }


    private String getEffectiveAttendanceTimeRange(ApsAttendanceParam attendanceParam) {
        // 定义初始时间段
        DateUtils.TimeRange baseTimeRange = new DateUtils.TimeRange(attendanceParam.getAttendanceTimeRange());
        // 定义后续时间段列表
        List<String> additionalTimeRanges = new ArrayList<>();
        additionalTimeRanges.add(attendanceParam.getLunchBreakTimeRange());
        additionalTimeRanges.add(attendanceParam.getDinnerTimeRange());
        additionalTimeRanges.add(attendanceParam.getMorningMeetingTimeRange());
        // 去除后续时间段
        List<String> remainingTimeRanges = DateUtils.subtractTimeRanges(baseTimeRange, additionalTimeRanges);
        return String.join(",", remainingTimeRanges);
    }

    private ApsTimeSheet buildTimeSheet(ApsAttendanceParam attendanceParam) {
        ApsTimeSheet apsTimeSheet = new ApsTimeSheet();
        apsTimeSheet.setAttendanceTimeRange(attendanceParam.getAttendanceTimeRange());
        apsTimeSheet.setLunchBreakTimeRange(attendanceParam.getLunchBreakTimeRange());
        apsTimeSheet.setDinnerTimeRange(attendanceParam.getDinnerTimeRange());
        apsTimeSheet.setMorningMeetingTimeRange(attendanceParam.getMorningMeetingTimeRange());
        return apsTimeSheet;
    }


    private ApsAttendance buildAttendancePo(ApsAttendanceParam attendanceParam, List<String> time) {
        ApsAttendance apsAttendance = new ApsAttendance();
        apsAttendance.setId(attendanceParam.getId());
        apsAttendance.setEmployeeName(attendanceParam.getEmployeeName());
        Date date = attendanceParam.getDate();
        apsAttendance.setDate(date);
        String dayOfWeek = DateUtils.getDayOfWeek(date);
        apsAttendance.setDayOfWeek(dayOfWeek);
        String formatDate = sdf.format(date);
        if (dateToHolidayMap.containsKey(formatDate)) {
            ApsHolidayTable holidayTable = dateToHolidayMap.get(formatDate);
            apsAttendance.setIsWorkday(!holidayTable.getIsHoliday());
        } else {
            // 如果不在节假日内，根据星期判断是否为工作日
            boolean isWorkday = isWeekday(dayOfWeek);
            apsAttendance.setIsWorkday(isWorkday);
        }
        apsAttendance.setLeaveTimeRange(attendanceParam.getLeaveTimeRange());
        apsAttendance.setEffectiveAttendanceTimeRange(String.join(",", time));
        return apsAttendance;
    }

    // 判断是否为工作日的方法
    private boolean isWeekday(String dayOfWeek) {
        // 在这里根据星期判断是否为工作日，你可以根据实际需求进行调整
        return !"星期日".equals(dayOfWeek) && !"星期六".equals(dayOfWeek);
    }


    private PageResultVo<ApsAttendanceVo> buildResultVo(Page<ApsAttendanceDto> attendanceVoPage) {
        List<ApsAttendanceVo> res = attendanceVoPage.getRecords().stream().map(x -> {
            ApsAttendanceVo apsAttendanceVo = new ApsAttendanceVo();
            apsAttendanceVo.setId(x.getId());
            apsAttendanceVo.setEmployeeName(x.getEmployeeName());
            apsAttendanceVo.setDate(x.getDate());
            apsAttendanceVo.setDayOfWeek(x.getDayOfWeek());
            apsAttendanceVo.setIsWorkday(x.getIsWorkday() ? "是" : "否");
            apsAttendanceVo.setAttendanceTimeRange(x.getAttendanceTimeRange());
            apsAttendanceVo.setLunchBreakTimeRange(x.getLunchBreakTimeRange());
            apsAttendanceVo.setDinnerTimeRange(x.getDinnerTimeRange());
            apsAttendanceVo.setMorningMeetingTimeRange(x.getMorningMeetingTimeRange());
            apsAttendanceVo.setLeaveTimeRange(x.getLeaveTimeRange());
            apsAttendanceVo.setEffectiveAttendanceTimeRange(x.getEffectiveAttendanceTimeRange());
            return apsAttendanceVo;
        }).collect(Collectors.toList());
        PageResultVo<ApsAttendanceVo> resultVo = new PageResultVo<>();
        resultVo.setSize(Math.toIntExact(attendanceVoPage.getSize()));
        resultVo.setPage(Math.toIntExact(attendanceVoPage.getCurrent()));
        resultVo.setPages(attendanceVoPage.getPages());
        resultVo.setTotal(attendanceVoPage.getTotal());
        resultVo.setList(res);
        return resultVo;
    }
}




