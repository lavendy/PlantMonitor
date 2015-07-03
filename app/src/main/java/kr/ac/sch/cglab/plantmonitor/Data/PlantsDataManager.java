package kr.ac.sch.cglab.plantmonitor.Data;

import java.util.ArrayList;


public class PlantsDataManager
{
    private enum INFO_TYPE { HUMIDITY,TEMPERATURE, LUX};


    public static String mUUID;
    public static ArrayList<PlantData> mPlantsList = new ArrayList<>();

    public int getPlnatsNum()
    {
        return mPlantsList.size();
    }

    public static String getPlantStatus(int num) {
        PlantData data = mPlantsList.get(num);

        //수정 할것
        //상태 읽어서 목표치에 도달 못하면 정리해서 문자열로 리턴 할걸
        //상태 측정 할것!
        String str = "양호";

        return str;
    }

    public static String getPlantName(int num)
    {
        PlantData data = mPlantsList.get(num);
        return  data.mPlantName;
    }

    public static int getPlantImgNum(int num)
    {
        PlantData data = mPlantsList.get(num);
        return data.mImgNum;
    }

    public static String getPlantHumidityInfo(int num)
    {
        //마지막 측정된 습도 정보 가져오기
        String str = getInfoString(num, INFO_TYPE.HUMIDITY);
        return str;
    }

    public static String getPlantTemperatrueInfo(int num)
    {
        //마지막 측정된 온도 정보 가져오기
        String str = getInfoString(num, INFO_TYPE.TEMPERATURE);
        return str;
    }

    public static String getPlantLuxInfo(int num)
    {
        //마지막 측정된 조도 정보 가져오기
        String str = getInfoString(num, INFO_TYPE.LUX);
        return str;
    }



    private static String getInfoString(int num, INFO_TYPE type)
    {
        PlantData data = mPlantsList.get(num);

        String str = "";
        int humidity = data.mLastedHumidity;
        int temperature = data.mLastedTemperature;
        int lux = data.mLastedLux;

        if(type == INFO_TYPE.HUMIDITY)
        {
            str = "습도 : " + humidity + "%";
        }
        else if(type == INFO_TYPE.TEMPERATURE)
        {
            str = "온도 : " + temperature + "'C";
        }
        else if(type == INFO_TYPE.LUX)
        {
            str = "조도 : " + lux + "Lux";
        }
        return str;
    }


}
