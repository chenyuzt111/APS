package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsAttendance;
import com.benewake.system.entity.ApsHolidayTable;
import com.benewake.system.entity.ApsTimeSheet;
import com.benewake.system.entity.vo.ApsAttendanceParam;
import com.benewake.system.entity.vo.ApsAttendanceVo;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsHolidayTableMapper;
import com.benewake.system.mapper.ApsTimeSheetMapper;
import com.benewake.system.service.ApsAttendanceService;
import com.benewake.system.mapper.ApsAttendanceMapper;
import com.benewake.system.service.ApsHolidayTableService;
import com.benewake.system.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
        Page<ApsAttendanceVo> apsAttendanceVoPage = new Page<ApsAttendanceVo>()
                .setCurrent(page)
                .setSize(size);

        Page<ApsAttendanceVo> attendanceVoPage = apsAttendanceMapper.getAttendancePage(apsAttendanceVoPage);
        return buildResultVo(attendanceVoPage);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addOrUpdateAttendance(ApsAttendanceParam attendanceParam) {
        Date date = attendanceParam.getDate();
        // 检查日期大小
        if (date != null && date.after(maxHolidayDate)) {
            // 抛出异常或执行其他操作
            throw new BeneWakeException("日期超过最大节假日日期");
        }
        ApsAttendance apsAttendance = buildAttendancePo(attendanceParam);
        if (apsAttendance.getId() == null) {
            return save(apsAttendance);
        } else {
            ApsTimeSheet apsTimeSheet = buildTimeSheet(attendanceParam);
            timeSheetMapper.delete(null);
            timeSheetMapper.insert(apsTimeSheet);
            return updateById(apsAttendance);
        }
    }

    private ApsTimeSheet buildTimeSheet(ApsAttendanceParam attendanceParam) {
        ApsTimeSheet apsTimeSheet = new ApsTimeSheet();
        apsTimeSheet.setAttendanceTimeRange(attendanceParam.getAttendanceTimeRange());
        apsTimeSheet.setLunchBreakTimeRange(attendanceParam.getLunchBreakTimeRange());
        apsTimeSheet.setDinnerTimeRange(attendanceParam.getDinnerTimeRange());
        apsTimeSheet.setMorningMeetingTimeRange(attendanceParam.getMorningMeetingTimeRange());
        return apsTimeSheet;
    }


    private ApsAttendance buildAttendancePo(ApsAttendanceParam attendanceParam) {
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
        apsAttendance.setEffectiveAttendanceTimeRange(attendanceParam.getEffectiveAttendanceTimeRange());
        return apsAttendance;
    }

    // 判断是否为工作日的方法
    private boolean isWeekday(String dayOfWeek) {
        // 在这里根据星期判断是否为工作日，你可以根据实际需求进行调整
        return !"星期日".equals(dayOfWeek) && !"星期六".equals(dayOfWeek);
    }


    private PageResultVo<ApsAttendanceVo> buildResultVo(Page<ApsAttendanceVo> attendanceVoPage) {
        PageResultVo<ApsAttendanceVo> resultVo = new PageResultVo<>();
        resultVo.setSize(Math.toIntExact(attendanceVoPage.getSize()));
        resultVo.setPage(Math.toIntExact(attendanceVoPage.getCurrent()));
        resultVo.setPages(attendanceVoPage.getPages());
        resultVo.setTotal(attendanceVoPage.getTotal());
        resultVo.setList(attendanceVoPage.getRecords());
        return resultVo;
    }
}




