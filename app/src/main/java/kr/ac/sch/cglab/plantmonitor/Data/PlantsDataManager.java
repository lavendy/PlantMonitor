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

        //���� �Ұ�
        //���� �о ��ǥġ�� ���� ���ϸ� �����ؼ� ���ڿ��� ���� �Ұ�
        //���� ���� �Ұ�!
        String str = "��ȣ";

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
        //������ ������ ���� ���� ��������
        String str = getInfoString(num, INFO_TYPE.HUMIDITY);
        return str;
    }

    public static String getPlantTemperatrueInfo(int num)
    {
        //������ ������ �µ� ���� ��������
        String str = getInfoString(num, INFO_TYPE.TEMPERATURE);
        return str;
    }

    public static String getPlantLuxInfo(int num)
    {
        //������ ������ ���� ���� ��������
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
            str = "���� : " + humidity + "%";
        }
        else if(type == INFO_TYPE.TEMPERATURE)
        {
            str = "�µ� : " + temperature + "'C";
        }
        else if(type == INFO_TYPE.LUX)
        {
            str = "���� : " + lux + "Lux";
        }
        return str;
    }


}
