package com.example.datedivideprogressbar;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeIntervalUtils {

    /**
     * 获取当前时间
     *
     * @param template 时间格式，默认为 "yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static String getCurrentDateString(String template) {
        if (TextUtils.isEmpty(template)) {
            template = "yyyy-MM-dd HH:mm:ss";// 大写"HH"：24小时制，小写"hh"：12小时制
        }

        SimpleDateFormat formatter = new SimpleDateFormat(template, Locale.getDefault());

        System.out.println("getCurrentDateString = " + formatter.format(new Date()));

        return formatter.format(new Date());
    }

    /**
     * 获取 2 个时间的自然年历的时间间隔
     *
     * @param nextTime     后面的时间，需要大于 previousTime，空则默认为当前时间
     * @param previousTime 前面的时间，空则默认为当前时间
     * @param format       时间格式，eg:"yyyy-MM-dd", "yyyy-MM-dd hh:mm:ss"
     * @return [年, 月, 日, 小时, 分钟, 秒]的数组
     */
    public static int[] getTimeIntervalArray(String nextTime, String previousTime, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        Date nextDate;
        Date previousDate;
        Calendar nextCalendar = Calendar.getInstance();
        Calendar previousCalendar = Calendar.getInstance();

        // 空则取当前时间
        try {
            nextDate = dateFormat.parse(TextUtils.isEmpty(nextTime) ? getCurrentDateString(format) : nextTime);
            nextCalendar.setTime(nextDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 空则取当前时间
        try {
            previousDate = dateFormat.parse(TextUtils.isEmpty(previousTime) ? getCurrentDateString(format) : previousTime);
            previousCalendar.setTime(previousDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return getTimeIntervalArray(nextCalendar, previousCalendar);
    }

    /**
     * 获取 2 个时间的自然年历的时间间隔
     *
     * @param nextDate     后面的时间，需要大于 previousDate
     * @param previousDate 前面的时间
     * @return [年, 月, 日, 小时, 分钟, 秒]的数组
     */
    public static int[] getTimeIntervalArray(Calendar nextDate, Calendar previousDate) {
        int year = nextDate.get(Calendar.YEAR) - previousDate.get(Calendar.YEAR);
        int month = nextDate.get(Calendar.MONTH) - previousDate.get(Calendar.MONTH);
        int day = nextDate.get(Calendar.DAY_OF_MONTH) - previousDate.get(Calendar.DAY_OF_MONTH);
        int hour = nextDate.get(Calendar.HOUR_OF_DAY) - previousDate.get(Calendar.HOUR_OF_DAY);// 24小时制
        int min = nextDate.get(Calendar.MINUTE) - previousDate.get(Calendar.MINUTE);
        int second = nextDate.get(Calendar.SECOND) - previousDate.get(Calendar.SECOND);

        boolean hasBorrowDay = false;// "时"是否向"天"借过一位

        if (second < 0) {
            second += 60;
            min--;
        }

        if (min < 0) {
            min += 60;
            hour--;
        }

        if (hour < 0) {
            hour += 24;
            day--;

            hasBorrowDay = true;
        }

        if (day < 0) {
            // 计算截止日期的上一个月有多少天，补上去
            Calendar tempDate = (Calendar) nextDate.clone();
            tempDate.add(Calendar.MONTH, -1);// 获取截止日期的上一个月
            day += tempDate.getActualMaximum(Calendar.DAY_OF_MONTH);

            // nextDate是月底最后一天，且day=这个月的天数，即是刚好一整个月，比如20160131~20160229，day=29，实则为1个月
            if (!hasBorrowDay
                    && nextDate.get(Calendar.DAY_OF_MONTH) == nextDate.getActualMaximum(Calendar.DAY_OF_MONTH)// 日期为月底最后一天
                    && day >= nextDate.getActualMaximum(Calendar.DAY_OF_MONTH)) {// day刚好是nextDate一个月的天数，或大于nextDate月的天数（比如2月可能只有28天）
                day = 0;// 因为这样判断是相当于刚好是整月了，那么不用向 month 借位，只需将 day 置 0
            } else {// 向month借一位
                month--;
            }
        }

        if (month < 0) {
            month += 12;
            year--;
        }

        return new int[]{year, month, day, hour, min, second};
    }
}
