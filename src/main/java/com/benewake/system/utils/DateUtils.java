package com.benewake.system.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateUtils {
    //    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//    private static final DateTimeFormatter chineseDayFormat = DateTimeFormatter.ofPattern("E");
    public static boolean validateTimeRanges(String timeRanges) {
        String[] ranges = timeRanges.split(",");
        for (String range : ranges) {
            if (!isValidTimeRange(range.trim())) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidTimeRange(String timeRange) {
        String[] times = timeRange.split("-");
        if (times.length != 2) {
            return false;
        }

        String startTime = times[0].trim();
        String endTime = times[1].trim();

        return isValidTime(startTime) && isValidTime(endTime) && compareTimes(startTime, endTime);
    }

    private static boolean isValidTime(String time) {
        // 这里可以添加更多的时间格式验证逻辑
        // 简化示例，假设时间格式为 HH:mm
        return time.matches("\\d{2}:\\d{2}");
    }

    private static boolean compareTimes(String startTime, String endTime) {
        // 比较开始时间和结束时间，确保结束时间不小于开始时间
        return startTime.compareTo(endTime) < 0;
    }


    public static String getDayOfWeek(Date date) {
        if (date == null) {
            return null;
        }

        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        return dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.CHINA);
    }

    public static List<String> subtractTimeRanges(TimeRange baseTimeRange, List<String> additionalTimeRanges) {
        List<String> remainingTimeRanges = new ArrayList<>();
        remainingTimeRanges.add(baseTimeRange.toString());

        for (String additionalTimeRange : additionalTimeRanges) {
            List<String> newRemainingTimeRanges = new ArrayList<>();

            for (String remainingTimeRange : remainingTimeRanges) {
                TimeRange remaining = new TimeRange(remainingTimeRange);
                TimeRange additional = new TimeRange(additionalTimeRange);

                if (additional.getStart().isBefore(remaining.getEnd()) && additional.getEnd().isAfter(remaining.getStart())) {
                    // 如果后续时间段与初始时间段有交集，则更新剩余时间段
                    if (additional.getStart().isAfter(remaining.getStart())) {
                        newRemainingTimeRanges.add(remaining.getStart() + "-" + additional.getStart());
                    }
                    if (additional.getEnd().isBefore(remaining.getEnd())) {
                        newRemainingTimeRanges.add(additional.getEnd() + "-" + remaining.getEnd());
                    }
                } else {
                    // 如果无交集，则保留原始剩余时间段
                    newRemainingTimeRanges.add(remainingTimeRange);
                }
            }

            remainingTimeRanges = newRemainingTimeRanges;
        }

        return remainingTimeRanges;
    }

    // 时间段类
    public static class TimeRange {
        private LocalTime start;
        private LocalTime end;

        // 构造函数，接受格式为"HH:mm-HH:mm"的字符串
        public TimeRange(String range) {
            String[] parts = range.split("-");
            this.start = LocalTime.parse(parts[0]);
            this.end = LocalTime.parse(parts[1]);
        }

        // 获取开始时间
        public LocalTime getStart() {
            return start;
        }

        // 获取结束时间
        public LocalTime getEnd() {
            return end;
        }

        // 将时间段对象转为字符串
        @Override
        public String toString() {
            return start + "-" + end;
        }
    }
}
