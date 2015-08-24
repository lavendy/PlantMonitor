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
    public static final String SENSING_SERVICE              = "0000cbbb-0000-1000-8000-00805f9b34fb";
    public static final String TIMER_SERVICE                = "0000cbbb-0000-1000-8000-00805f9b34fb";

    //
    public static final String SENSING_CONTROL_SERVICE      = "400da2b0-a392-4082-8315-002e77c713f8";


    //attribute characteristics
    public static final String SENSING_DATA_MEASUREMENT     = "0000cbb1-0000-1000-8000-00805f9b34fb";
    //public static final String TEMPERATURE_MEASUREMENT    = "";
    //public static final String HUMIDITY_MEASUREMENT       = "";

    public static final String RTC_TIMER = "";

    //
    public static final String CONTROL_HUMIDITY             = "89a11740-bedb-4496-a000-b923e29b1de0";   //limit humidity
    public static final String CONTROL_LUX                  = "44f969fd-da27-455e-8b95-1c46e662700c";    //min max
    public static final String CONTROL_TEMPERATURE          = "8563a12d-4bec-4439-a2e5-01b9bb828c88";
}
