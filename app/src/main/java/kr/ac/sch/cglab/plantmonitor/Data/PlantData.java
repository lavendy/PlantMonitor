package kr.ac.sch.cglab.plantmonitor.Data;

import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;

import java.util.ArrayList;

public class PlantData {
    public String mPlantName = "";
    public String mPlantNum = "";

    public ArrayList<MeasuredData> mDataList;   //������ �½����� �����͵�

    public int mGoalHumidity = 0;
    public int mGoalTemperature = 0;
    public int mGoalLux = 0;

    public BluetoothDevice mDevice;

    public ContentValues mContentValue; //��� ���� �뵵

    public String getDeviceName()
    {
        return mDevice.getName();
    }
    public String getDeviceMacAddress()
    {
        return mDevice.getAddress();
    }

    public ContentValues getContentValue()
    {
        mContentValue = new ContentValues();
        mContentValue.put(PlantDBAdapter.DEVICE_NAME, this.getDeviceName()); //device name
        mContentValue.put(PlantDBAdapter.DEVICE_ADDRESS, this.getDeviceMacAddress());    //device mac address
        mContentValue.put(PlantDBAdapter.PLANT_NAME, this.mPlantName);       //�̸�
        mContentValue.put(PlantDBAdapter.PLANT_NUM, this.mPlantNum);         //�Ĺ� �ν� ��ȣ
        mContentValue.put(PlantDBAdapter.PLANT_GOAL_HUMIDITY, this.mGoalHumidity);   // ��ǥ ����
        mContentValue.put(PlantDBAdapter.PLANT_GOAL_TEMPERATURE, this.mGoalTemperature); //��ǥ �µ�
        mContentValue.put(PlantDBAdapter.PLANT_GOAL_LUX, this.mGoalLux); //��ǥ ����

        return mContentValue;
    }

    public class MeasuredData
    {
        public int mHumidity;
        public int mTemperature;
        public int mLux;
        public String mTime;
    }
}
