package kr.ac.sch.cglab.plantmonitor.Data;

import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;

import java.util.ArrayList;

public class PlantData {
    public String mPlantName = "";
    public String mPlantNum = "";
    public int mImgNum = -1;

    public ArrayList<MeasuredData> mDataList;   //측정된 온습조도 데이터들

    public int mGoalHumidity = 0;
    public int mGoalTemperature = 0;
    public int mGoalLux = 0;

    public int mLastedHumidity = 0;
    public int mLastedTemperature = 0;
    public int mLastedLux = 0;

    public BluetoothDevice mDevice;

    public ContentValues mContentValue; //디비 저장 용도

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
        mContentValue.put(PlantDBAdapter.PLANT_NAME, this.mPlantName);       //이름
        mContentValue.put(PlantDBAdapter.PLANT_NUM, this.mPlantNum);         //식물 인식 번호
        mContentValue.put(PlantDBAdapter.PLANT_IMG_NUM, this.mImgNum);      //이미지 식별 번호
        mContentValue.put(PlantDBAdapter.PLANT_GOAL_HUMIDITY, this.mGoalHumidity);   // 목표 습도
        mContentValue.put(PlantDBAdapter.PLANT_GOAL_TEMPERATURE, this.mGoalTemperature); //목표 온도
        mContentValue.put(PlantDBAdapter.PLANT_GOAL_LUX, this.mGoalLux); //목표 조도


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
