package com.avantir.wpos.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by lekanomotayo on 10/02/2018.
 */
public class TimeUtil {

    public static boolean hasExpired(String expDate){
        String expYr = TimeUtil.getYearFromExpDate(expDate);
        String expMon = TimeUtil.getMonthFromExpDate(expDate);
        int expYrInt = Integer.parseInt(expYr);
        int expMonInt = Integer.parseInt(expMon);

        int currYrInt = Calendar.getInstance().get(Calendar.YEAR);
        int currMonInt = Calendar.getInstance().get(Calendar.MONTH) + 1;

        if(expYrInt > currYrInt)
            return false;

        if(expYrInt == currYrInt){
            if(expMonInt > currMonInt)
                return false;

            if(expMonInt == currMonInt){
                int dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                int lastDayOfCurrMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
                if(dayOfMonth < lastDayOfCurrMonth)
                    return false;
            }
        }

        return true;
    }


    public static long getTimeInEpoch(Date date) {
        try{
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdfDate.setTimeZone(TimeZone.getTimeZone("Africa/Lagos"));
            Date newDate = sdfDate.parse(sdfDate.format(date));
            return newDate.getTime() / 1000;
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return System.currentTimeMillis();
    }


    public static long getStartOfDay(long epoch){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(epoch);
        cal.set(Calendar.HOUR_OF_DAY, 0); //set hours to zero
        cal.set(Calendar.MINUTE, 0); // set minutes to zero
        cal.set(Calendar.SECOND, 0); //set seconds to zero
        //Log.i("Time", cal.getTime().toString());
        return cal.getTimeInMillis() / 1000;
    }

    public static long getEndOfDay(long epoch){
        return getStartOfDay(epoch) * (24 * 60 * 60);
    }

    public static String getYearFromExpDate(String expDate){
        String expYr = expDate.substring(0, 2);
        String currentYr = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        String fullExpYr = currentYr.substring(0,2) + expYr;
        return fullExpYr;
    }

    public static String getMonthFromExpDate(String expDate){
        String expMon = expDate.substring(2);
        return expMon;
    }

    public static String getDateTimeMMddhhmmss(Date date){
        SimpleDateFormat sdfDate = new SimpleDateFormat("MMddHHmmss");
        sdfDate.setTimeZone(TimeZone.getTimeZone("Africa/Lagos"));
        String strDate = sdfDate.format(date);
        return strDate;
    }

    public static String getTimehhmmss(Date date){
        SimpleDateFormat sdfDate = new SimpleDateFormat("HHmmss");
        sdfDate.setTimeZone(TimeZone.getTimeZone("Africa/Lagos"));
        String strDate = sdfDate.format(date);
        return strDate;
    }

    public static String getDateMMdd(Date date){
        SimpleDateFormat sdfDate = new SimpleDateFormat("MMdd");
        sdfDate.setTimeZone(TimeZone.getTimeZone("Africa/Lagos"));
        String strDate = sdfDate.format(date);
        return strDate;
    }


}
