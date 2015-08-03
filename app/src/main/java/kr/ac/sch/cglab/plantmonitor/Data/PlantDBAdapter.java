package kr.ac.sch.cglab.plantmonitor.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;


public class PlantDBAdapter
{
    public static final String DATABASE_NAME = "plant_db";
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_TABLE_PHONE_INFO = "phone_info";
    //폰정보 데이블
    public static final String PHONE_UUID = "uuid";             //폰 uuid
    public static final String DEVICE_NUMBER = "device_num";    //디바이스 개수
    public static final String UPDATE_TIME = "update_time";     //마지막 업데이트 시간

    public static final String DATABASE_TABLE_MONITOR_DEVICE_INFO = "monitor_device";
    //식물 모니터링 디바이스 정보 테이블
    public static final String DEVICE_ADDRESS = "mac_address";  //디방이스 맥주소
    public static final String DEVICE_NAME = "device_name";     //디바이스 이름
    public static final String PLANT_NAME = "plant_name";       //식물 이름
    public static final String PLANT_NUM = "plant_num";         //식물 정보 번호
    public static final String PLANT_IMG_NUM = "plant_img_num"; //이미지 번호
    public static final String PLANT_GOAL_TEMPERATURE_MIN = "goal_temperature_min"; // 온도 측정 목표
    public static final String PLANT_GOAL_TEMPERATURE_MAX = "goal_temperature_max";
    public static final String PLANT_GOAL_HUMIDITY = "goal_humidity";       //습도 측정 목표
    public static final String PLANT_GOAL_LUX_MIN = "goal_lux_min";                 //조도 측정 목표
    public static final String PLANT_GOAL_LUX_MAX = "goal_lux_max";

    public static final String DATABASE_TABLE_MEASURED_DATA = "measured_data";
    //측정 데이터 테이블
    //public static final String PLANT_NUM = "plant_num";         //식물 정보 번호
    public static final String PLANT_MEASURED_TIME = "measured_time";   //측정 시간
    public static final String PLANT_MEASURED_HUMIDITY = "humidity";    //측정 습도
    public static final String PLANT_MEASURED_LUX = "lux";              //측정 조도
    public static final String PLANT_MEASURED_TEMPERATURE = "temperature";  //측정 온도

    //쿼리 정의
    private static final String TABLE_CREATE_PHONE_INFO = "CREATE TABLE "+
            DATABASE_TABLE_PHONE_INFO + "(" +
            " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PHONE_UUID + " TEXT , " +
            DEVICE_NUMBER + " INTEGER, " +
            UPDATE_TIME + " TEXT " + ")";

    private static final String TABLE_CREATE_MONITOR_DEVICE_INFO = "CREATE TABLE "+
            DATABASE_TABLE_MONITOR_DEVICE_INFO + "(" +
            " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DEVICE_ADDRESS + " TEXT , " +
            DEVICE_NAME + " TEXT , " +
            PLANT_NUM + " INTEGER, " +
            PLANT_NAME + " TEXT , " +
            PLANT_IMG_NUM + " INTEGER , " +
            PLANT_GOAL_TEMPERATURE_MIN + " INTEGER, " +
            PLANT_GOAL_TEMPERATURE_MAX + " INTEGER, " +
            PLANT_GOAL_HUMIDITY + " INTEGER, "+
            PLANT_GOAL_LUX_MIN + " INTEGER " +
            PLANT_GOAL_LUX_MAX + " INTEGER " + ")";

    private static final String TABLE_CREATE_MEASURED_DATA = "CREATE TABLE "+
            DATABASE_TABLE_MEASURED_DATA + "(" +
            " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PLANT_NUM + " INTEGER, " +
            PLANT_MEASURED_TIME + " TEXT , " +
            PLANT_MEASURED_TEMPERATURE + " INTEGER, " +
            PLANT_MEASURED_HUMIDITY + "INTEGER, " +
            PLANT_MEASURED_LUX + " INTEGER " + ")";

    private SQLiteDatabase mDBWriter;
    private SQLiteDatabase mDBReader;
    private DatabaseHelper mDBHelper;
    private Context mContext = null;

    //생성자
    public PlantDBAdapter(Context context)
    {
        this.mContext = context;
    }

    public PlantDBAdapter open() throws SQLException
    {
        mDBHelper = new DatabaseHelper(this.mContext);
        this.mDBWriter = mDBHelper.getWritableDatabase();
        this.mDBReader = mDBHelper.getReadableDatabase();
        return this;
    }

    public void close()
    {
        mDBHelper.close();
    }

    //uuid 디바이스 개수, 마지막 업데이트 시간 저장
    public long createPhoneInfo(String uuid, int count, String time)
    {
        //데이터가 존재 하면 건너 뜀
        Cursor c = mDBReader.query(DATABASE_TABLE_PHONE_INFO, null,null,null,null,null,null );
        if(c.getCount() >= 1)
            return 0;

        ContentValues initValues = new ContentValues();
        initValues.put(PHONE_UUID, uuid);
        initValues.put(DEVICE_NUMBER, count);
        initValues.put(UPDATE_TIME, time);
        return mDBWriter.insert(DATABASE_TABLE_PHONE_INFO, null, initValues);
    }
    public String getPhoneInfo()
    {
        String str = "";
        Cursor c = mDBReader.query(DATABASE_TABLE_PHONE_INFO, null,null,null,null,null,null );
        if(c.getCount() == 0)
            return "phone_info has no data";
        else
        {
            c.moveToFirst();
            for (int i = 1; i < c.getColumnCount(); ++i)
                str = str.concat(c.getString(i) + " ");
        }
        return str;
    }

    public long updateLastUpdatedTime(String time)
    {
        ContentValues updateValues = new ContentValues();
        updateValues.put(UPDATE_TIME, time);
        return mDBWriter.update(DATABASE_TABLE_PHONE_INFO, updateValues, "_id = 1", null);
    }


    public long createNewPlant(PlantData plantData)
    {
        //디바이스 추가
        ContentValues initValues = plantData.getContentValue();     //디비용으로 저장된 디바이스 데이터
        return mDBWriter.insert(DATABASE_TABLE_MONITOR_DEVICE_INFO, null, initValues);
    }
    public long createNewData(PlantData plantData)
    {
        long rs = 0;
        //측정된 데이터 전부 쑤셔 넣기
        for(PlantData.MeasuredData data : plantData.mDataList)
        {
            ContentValues initValues = new ContentValues();
            initValues.put(PLANT_NUM, plantData.mPlantNum);
            initValues.put(PLANT_MEASURED_TIME, data.mTime);
            initValues.put(PLANT_MEASURED_HUMIDITY, data.mHumidity);
            initValues.put(PLANT_MEASURED_TEMPERATURE, data.mTemperature);
            initValues.put(PLANT_MEASURED_LUX, data.mLux);
            rs = mDBWriter.insert(DATABASE_TABLE_MEASURED_DATA, null, initValues);
            if(rs == -1)
                return  -1;
        }
        return rs;
    }



    public long updatePlantInfo(PlantData plantData)
    {
        ContentValues updateValues = plantData.getContentValue();   //디비용으로 저장된 디바이스 데이터
        return mDBWriter.update(DATABASE_TABLE_MONITOR_DEVICE_INFO,       //db 내용중 식물 넘버랑 같은게 있으면 업데이트
                updateValues, PLANT_NUM + " = " + plantData.mPlantNum, null); //plant_num = 10
    }


    public long deletePhoneInfo(long rowID){
        return mDBWriter.delete(DATABASE_TABLE_PHONE_INFO, null, null);   //전체 삭제
    }
    public long deletePlantInfo(PlantData plantData)
    {
        return mDBWriter.delete(DATABASE_TABLE_MONITOR_DEVICE_INFO, PLANT_NUM + " = " + plantData.mPlantNum, null) ;
    }
    public long deleteMesasuredData(String time, int plantNum)
    {
        //측정 시간과 식물 넘버로 검색
        return mDBWriter.delete(DATABASE_TABLE_MEASURED_DATA, PLANT_NUM + " = " + plantNum + " and " + PLANT_MEASURED_TIME + " = " + time, null);
    }




    //커스텀 헬퍼 클래스
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //테이블 생성
            db.execSQL(TABLE_CREATE_PHONE_INFO);
            db.execSQL(TABLE_CREATE_MONITOR_DEVICE_INFO);
            db.execSQL(TABLE_CREATE_MEASURED_DATA);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //다른 버전으로 생성시 기존 테이블을 삭제 후 새 테이블 생성
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_PHONE_INFO);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_MONITOR_DEVICE_INFO);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_MEASURED_DATA);

            onCreate(db);
        }
    }

}
