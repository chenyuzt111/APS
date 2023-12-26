package com.benewake.system.excel.transfer;

import com.benewake.system.entity.ApsAttendance;
import com.benewake.system.entity.ApsFinishedProductBasicData;
import com.benewake.system.entity.ApsHolidayTable;
import com.benewake.system.entity.ApsTimeSheet;
import com.benewake.system.excel.entity.AttendanceTemplate;
import com.benewake.system.excel.entity.ExcelFinishedProductTemplate;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.benewake.system.service.impl.ApsAttendanceServiceImpl.dateToHolidayMap;
import static com.benewake.system.service.impl.ApsAttendanceServiceImpl.maxHolidayDate;

@Component
public class AttendanceExcelToPo {


    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public ApsAttendance convert(AttendanceTemplate object, ApsTimeSheet timeSheet) {
        if (object == null) {
            return null;
        }
        Date date = object.getDate();
        // 检查日期大小
        if (date != null && date.after(maxHolidayDate)) {
            // 抛出异常或执行其他操作
            //todo 节假日做个定制化的页面 显示 并可手动修改节假日
            throw new BeneWakeException(sdf.format(date) + "当前日期超过目前获取节假日的最大日期");
        }
        ApsAttendance apsAttendance = new ApsAttendance();
        apsAttendance.setEmployeeName(object.getEmployeeName());
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
        apsAttendance.setLeaveTimeRange(object.getLeaveTimeRange());
        if (timeSheet != null) {
            List<String> time = getEffectiveAttendanceTime(timeSheet, object.getLeaveTimeRange());
            apsAttendance.setEffectiveAttendanceTimeRange(String.join(",", time));
        }
        return apsAttendance;

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


    // 判断是否为工作日的方法
    private boolean isWeekday(String dayOfWeek) {
        // 在这里根据星期判断是否为工作日，你可以根据实际需求进行调整
        return !"星期日".equals(dayOfWeek) && !"星期六".equals(dayOfWeek);
    }

}
