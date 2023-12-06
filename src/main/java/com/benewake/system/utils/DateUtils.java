package com.benewake.system.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");

    public static String getDayOfWeek(Date date) {
        if (date == null) {
            return null;
        }

        String dayOfWeek = dayFormat.format(date);
        return dayOfWeek;
    }
}
