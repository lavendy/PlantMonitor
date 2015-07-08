package kr.ac.sch.cglab.plantmonitor.Data;

import java.security.Policy;
import java.util.ArrayList;


public class PlantsDataManager
{
    public static enum INFO_TYPE { HUMIDITY,TEMPERATURE, LUX};
    public static enum PROPERTIES_STATUS {LOW, NORMAL, HIGH };

    public static String mUUID;
    public static ArrayList<PlantData> mPlantsList = new ArrayList<>();

    public int getPlnatsNum()
    {
        return mPlantsList.size();
    }

    public static String getPlantStatus(int num) {
        PlantData data = mPlantsList.get(num);

        //수정 할것
        String str = "디바이스를 찾을 수 없음";

        if(data.mIsConnected == false)   //디바이스가 연결 되어 있다면 혹은 근처에 존재
        {
            str = "토양 습도 : ";
            //상태 읽어서 목표치에 도달 못하면 정리해서 문자열로 리턴 할걸
            //상태 측정 할것!
            PROPERTIES_STATUS status = checkHumidity(num);
            if(status == PROPERTIES_STATUS.LOW)
                str += "물이 부족 |";
            else if(status == PROPERTIES_STATUS.NORMAL)
                str += "양호 |";
            else
                str += "너무 습함 |";

            status = checkTemperature(num);
            str += " 온도 : ";
            if(status == PROPERTIES_STATUS.LOW)
                str += "추움 |";
            else if(status == PROPERTIES_STATUS.NORMAL)
                str += "적당함 |";
            else
                str += "더움 |";

            status = checkLux(num);
            str += " 밝기 : ";
            if(status == PROPERTIES_STATUS.LOW)
                str += "너무 어두움";
            else if(status == PROPERTIES_STATUS.NORMAL)
                str += "양호";
            else
                str += "너무 밝음";

            str += "..";
        }
        return str;
    }


    public static PROPERTIES_STATUS checkTemperature(int pos)
    {
        //온도 평가
        PlantData data = mPlantsList.get(pos);
        if(data.mLastedTemperature < data.mGoalTemperatureMin)
            return PROPERTIES_STATUS.LOW;
        else if(data.mGoalTemperatureMin <= data.mLastedTemperature && data.mLastedTemperature < data.mGoalTemperatureMax)
            return PROPERTIES_STATUS.NORMAL;
        else
            return PROPERTIES_STATUS.HIGH;
    }
    public static PROPERTIES_STATUS checkHumidity(int pos)
    {
        PlantData data = mPlantsList.get(pos);
        if(data.mLastedHumidity < data.mGoalHumidity - 10)  //+=10% 이내로 측정
            return PROPERTIES_STATUS.LOW;
        else if(data.mGoalHumidity - 10 <= data.mLastedHumidity && data.mLastedHumidity < data.mGoalHumidity + 10)
            return PROPERTIES_STATUS.NORMAL;
        else
            return PROPERTIES_STATUS.HIGH;
    }
    public static PROPERTIES_STATUS checkLux(int pos)
    {
        PlantData data = mPlantsList.get(pos);
        if(data.mLastedLux < data.mGoalLuxMin)
            return PROPERTIES_STATUS.LOW;
        else if(data.mGoalLuxMin <= data.mLastedLux && data.mLastedLux < data.mGoalLuxMax)
            return PROPERTIES_STATUS.NORMAL;
        else
            return PROPERTIES_STATUS.HIGH;
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


    public static String getPlantHumidityStr(int num)
    {
        //마지막 측정된 습도 정보 가져오기
        String str = getInfoString(num, INFO_TYPE.HUMIDITY);
        return str;
    }

    public static String getPlantTemperatureStr(int num)
    {
        //마지막 측정된 온도 정보 가져오기
        String str = getInfoString(num, INFO_TYPE.TEMPERATURE);
        return str;
    }

    public static String getPlantLuxStr(int num)
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
            str = "습도 : " + humidity + " %";
        }
        else if(type == INFO_TYPE.TEMPERATURE)
        {
            str = "온도 : " + temperature + "'C";
        }
        else if(type == INFO_TYPE.LUX)
        {
            PROPERTIES_STATUS sts = checkLux(num);
            if(sts == PROPERTIES_STATUS.LOW)
                str = "밝기 : 어두움";
            else if(sts == PROPERTIES_STATUS.NORMAL)
                str = "밝기 : 알맞음";
            else
                str = "밝기 : 너무밝음";
            //str = "조도 : " + lux / 100 + "kLux";
        }
        return str;
    }

    public static boolean isConnected(int num)
    {
        return mPlantsList.get(num).mIsConnected;
    }

    public static int getPlantHumidity(int pos)
    {
        return mPlantsList.get(pos).mLastedHumidity;
    }

    public static int getPlantTemperature(int pos)
    {
        return mPlantsList.get(pos).mLastedTemperature;
    }

    public static int getPlantLux(int pos)
    {
        return mPlantsList.get(pos).mLastedLux;
    }

    public static int getPlantGoalHumidity(int pos)
    {
        return mPlantsList.get(pos).mGoalHumidity;
    }

    public static int getPlantGoalTemperature(int pos)
    {
        return mPlantsList.get(pos).mGoalTemperatureMin;
    }

    public static int getPlantGoalLux(int pos)
    {
        return mPlantsList.get(pos).mGoalLuxMin;
    }
}
