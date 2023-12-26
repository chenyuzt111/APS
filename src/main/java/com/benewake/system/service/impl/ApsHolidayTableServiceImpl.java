package com.benewake.system.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsHolidayTable;
import com.benewake.system.service.ApsHolidayTableService;
import com.benewake.system.mapper.ApsHolidayTableMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author ASUS
 * @description 针对表【aps_holiday_table】的数据库操作Service实现
 * @createDate 2023-12-06 10:31:16
 */
@Service
public class ApsHolidayTableServiceImpl extends ServiceImpl<ApsHolidayTableMapper, ApsHolidayTable>
        implements ApsHolidayTableService {

    private final String HOLIDAY_URL = "http://timor.tech/api/holiday/year/";

    @Override
    public void updateHoliday() {
        try {
            LocalDate currentDate = LocalDate.now();
            // 获取当前年份
            int currentYear = currentDate.getYear();
            // 获取下一年的年份
            int nextYear = currentYear + 1;
            updateHolidayByYear(currentYear);
            updateHolidayByYear(nextYear);
            init();
        } catch (Exception e) {
            log.error("节假日第三方调用接口超时" + e.getMessage());
        }

    }

    private void init() {
        List<ApsHolidayTable> holidayTables = baseMapper.selectList(null);
        Optional<Date> maxDate = holidayTables.stream()
                .map(obj -> Date.from(LocalDate.parse(obj.getYear() + "-" + obj.getMonthDay())
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .max(Date::compareTo);

        maxDate.ifPresent(date -> {
            // 在这里处理最大日期
            ApsAttendanceServiceImpl.maxHolidayDate = date;
        });
        ApsAttendanceServiceImpl.dateToHolidayMap = holidayTables.stream().collect(Collectors.toMap(
                obj -> obj.getYear() + "-" + obj.getMonthDay(),
                obj -> obj
        ));
    }

    private void updateHolidayByYear(Integer currentYear) {
        String url = HOLIDAY_URL + currentYear;
        HttpRequest get = HttpUtil.createGet(url);
        HttpResponse execute = get.execute();
        String s = execute.body();
        if (StringUtils.isNotEmpty(s)) {
            List<ApsHolidayTable> holidayTables = new ArrayList<>();
            JSONObject jsonObject = JSONUtil.parseObj(s);
            Object holiday = jsonObject.get("holiday");
            JSONObject holidayJsonObj = JSONUtil.parseObj(holiday);
            for (Map.Entry<String, Object> entry : holidayJsonObj.entrySet()) {
                String date = entry.getKey();
                JSONObject holidayInfo = (JSONObject) entry.getValue();
                // 提取节假日信息
                int year = Integer.parseInt(holidayInfo.get("date").toString().split("-")[0]);
                String monthDay = date;
                boolean isHoliday = holidayInfo.getBool("holiday");
                String name = holidayInfo.getStr("name");
                ApsHolidayTable holidayTable = new ApsHolidayTable();
                holidayTable.setMonthDay(monthDay);
                holidayTable.setIsHoliday(isHoliday);
                holidayTable.setName(name);
                holidayTable.setYear(year);
                holidayTables.add(holidayTable);
            }
            LambdaQueryWrapper<ApsHolidayTable> queryWrapper = new LambdaQueryWrapper<ApsHolidayTable>()
                    .eq(ApsHolidayTable::getYear, currentYear);
            remove(queryWrapper);
            saveBatch(holidayTables);
        }
    }
}




