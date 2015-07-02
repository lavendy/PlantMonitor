package kr.ac.sch.cglab.plantmonitor.Data;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.text.SimpleDateFormat;
import java.util.Date;


public class PhoneInfoManager {

    //get datetime format = 201504251132 /년월일시분
    public static String getTime()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        return dateFormat.format(date);
    }
    //get uuid
    public static String getUUID(Context context)
    {
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELECOM_SERVICE);
        return tManager.getDeviceId();
    }



}
