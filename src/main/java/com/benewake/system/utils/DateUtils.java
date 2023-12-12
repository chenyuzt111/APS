package com.benewake.system.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {
//    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//    private static final DateTimeFormatter chineseDayFormat = DateTimeFormatter.ofPattern("E");

    public static String getDayOfWeek(Date date) {
        if (date == null) {
            return null;
        }

        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        return dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.CHINA);
    }
}
