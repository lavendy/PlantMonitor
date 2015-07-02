package kr.ac.sch.cglab.plantmonitor.Data;

import java.util.ArrayList;


public class PlantsDataManager {
    public static String mUUID;
    public static ArrayList<PlantData> mPlantsList = new ArrayList<>();

    public int getPlnatsNum()
    {
        return mPlantsList.size();
    }
}
