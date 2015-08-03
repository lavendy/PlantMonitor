package kr.ac.sch.cglab.plantmonitor.BLE;

import java.util.UUID;

/**
 * Created by Administrator on 2015-07-14.
 */
public class GattAttributes
{
    //for default attribute
    public static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    //attribute services
    public static final String SENSING_SERVICE              = "d4ee2020-39a9-11e5-a71f-0002a5d5c51b";
    public static final String TIMER_SERVICE                = "";

    //attribute characteristics
    public static final String SENSING_DATA_MEASUREMENT     = "95b62040-39ab-11e5-b0b9-0002a5d5c51b";
    //public static final String TEMPERATURE_MEASUREMENT    = "";
    //public static final String HUMIDITY_MEASUREMENT       = "";

    public static final String RTC_TIMER = "";


    //uuid for services;
    public static final UUID UUID_SERVICE_SENSING               = UUID.fromString(SENSING_SERVICE);
    public static final UUID UUID_SERVICE_TIMER                 = UUID.fromString(TIMER_SERVICE);

    //uuid for characteristics
    public static final UUID UUID_CHARACTERISTIC_SENSING_DATA   = UUID.fromString(SENSING_DATA_MEASUREMENT);
    public static final UUID UUID_CHARACTERISTIC_RTC_TIMER      = UUID.fromString(RTC_TIMER);

    //uuid for common setting
    public static final UUID UUID_CLIENT_CHARACTERISTIC_CONFIG  = UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG);


}
