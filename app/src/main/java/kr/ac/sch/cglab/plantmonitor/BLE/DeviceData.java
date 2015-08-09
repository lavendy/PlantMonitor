package kr.ac.sch.cglab.plantmonitor.BLE;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Administrator on 2015-08-09.
 */
public class DeviceData
{
    public BluetoothDevice mDevice;
    public int mCurrentTemperature;
    public int mCurrentHumidity;
    public int mCurrentLux;

    public DeviceData(BluetoothDevice device, int temperature, int humidity, int lux)
    {
        mDevice = device;
        mCurrentTemperature = temperature;
        mCurrentHumidity = humidity;
        mCurrentLux = lux;
    }
    public DeviceData(){}
}
