package kr.ac.sch.cglab.plantmonitor.Data;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class PhoneInfoManager {

    //get datetime format = 201504251132 /년월일시분
    public static String getTime()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        return dateFormat.format(date);
    }
    //get uuid
    public static String getUUID(Context context) {

        TelephonyManager TelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String devID = "" + TelephonyManager.getDeviceId();
        String serial = "" + TelephonyManager.getSimSerialNumber();
        String phID = "" + android.provider.Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID);

        UUID deviceUUID = new UUID(phID.hashCode(), (devID.hashCode() << 32) | serial.hashCode());

        return deviceUUID.toString();
    }



}
