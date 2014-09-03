package com.zjut.bluetoothle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeHelper {
    static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static SimpleDateFormat sdf2=new SimpleDateFormat("yyyyMMddHHmmss");
    private static Date baseData=new Date(100,0,1,0,0,0);
    private static Date targetData=null;

    public static String getDatetime()
    {
        return sdf2.format(new Date());
    }

    public static String secondToDate(long second)
    {

        //baseData=sdf.parse(baseDateString);
        //System.out.println(baseData);
        targetData=new Date(baseData.getTime()+second*1000);
        //System.out.println(targetData);
        return sdf.format(targetData);
    }

    public static String secondToDate(String secondString)
    {
        long second=Long.parseLong(secondString);
        return secondToDate(second);
    }

    public static String dateToSecond(String dateString)
    {
        Date date=new Date();
        long second=0;

        try {
            date=sdf.parse(dateString);
            second=(date.getTime()-baseData.getTime())/1000;
            //baseData=sdf.parse(baseDateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return String.valueOf(second);
    }

    public static String dateToSecond(Date date)
    {
        long second=0;
        second=(date.getTime()-baseData.getTime())/1000;
        return String.valueOf(second);
    }
    public static String dateToSecond()
    {
        long second=0;
        //System.out.println(baseData.getTime()/1000);
        second=(new Date().getTime()-baseData.getTime())/1000;
        return String.valueOf(second);
    }

    public static int compareTime(int deviceTime)
    {
        long compare=0;
        compare=deviceTime-(new Date().getTime()-baseData.getTime())/1000;
        return (int) compare;
    }

    public static String compareString(int deviceTime)
    {
        String string=null;
        int compare=compareTime(deviceTime);
        if (Math.abs(compare)>5) {
            string="未同步";
        }
        else {
            string="已同步";
        }
        return string;
    }


    public static String minuteToDate(String minuteString)
    {
        Long minute=Long.parseLong(minuteString);
        return secondToDate(minute*60);
    }
}
