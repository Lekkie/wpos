package com.avantir.wpos.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by lekanomotayo on 10/02/2018.
 */
public class TimeUtil {

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
