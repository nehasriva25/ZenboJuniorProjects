/*
    Util functions for formatting the date on the calender and the time when medication is taken
 */
package com.example.medicationdemo;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DayUtil {

    public static Calendar currentDate;

    public static String getDay()
    {
        return "date.format(formatter)";
    }

    public static String getMonthAndYear(Calendar currDate){
        SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy");
        return format.format(currDate.getTime());
    }

    public static String getWeekDay(Calendar currDate){
        SimpleDateFormat format = new SimpleDateFormat("EEEE");
        return format.format(currDate.getTime());
    }

    public static String getDate(Calendar currDate){
        SimpleDateFormat format = new SimpleDateFormat("d");
        return format.format(currDate.getTime());
    }

    public static String getHourMinute (Calendar currentDate){
        SimpleDateFormat format = new SimpleDateFormat("kk:mm a");
        return format.format(currentDate.getTime());
    }

    public static String getHourTime(int hour){
        String hourString;
        if (hour < 12){
            hourString = hour+":00 AM";
        }else if (hour > 12){
            hour -= 12;
            hourString = hour+":00 PM";
        } else {
            hourString = hour+":00 PM";
        }

        return hourString;

    }


}
