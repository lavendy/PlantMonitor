package kr.ac.sch.cglab.plantmonitor.NewPlant;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Administrator on 2015-06-18.
 */
public class ScannedBluetoothDevice {
    public BluetoothDevice mDevice;
    public int mRSSI;

    public ScannedBluetoothDevice(BluetoothDevice device, int rssi){
        this.mDevice = device;
        this.mRSSI = rssi;
    }
}
