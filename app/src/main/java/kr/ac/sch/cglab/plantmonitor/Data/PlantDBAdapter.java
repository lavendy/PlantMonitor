package kr.ac.sch.cglab.plantmonitor.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;


public class PlantDBAdapter
{
    public static final String DATABASE_NAME = "plant_db";
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_TABLE_PHONE_INFO = "phone_info";
    //������ ���̺�
    public static final String PHONE_UUID = "uuid";             //�� uuid
    public static final String DEVICE_NUMBER = "device_num";    //����̽� ����
    public static final String UPDATE_TIME = "update_time";     //������ ������Ʈ �ð�

    public static final String DATABASE_TABLE_MONITOR_DEVICE_INFO = "monitor_device";
    //�Ĺ� ����͸� ����̽� ���� ���̺�
    public static final String DEVICE_ADDRESS = "mac_address";  //����̽� ���ּ�
    public static final String DEVICE_NAME = "device_name";     //����̽� �̸�
    public static final String PLANT_NAME = "plant_name";       //�Ĺ� �̸�
    public static final String PLANT_NUM = "plant_num";         //�Ĺ� ���� ��ȣ
    public static final String PLANT_IMG_NUM = "plant_img_num"; //�̹��� ��ȣ
    public static final String PLANT_GOAL_TEMPERATURE = "goal_temperature"; // �µ� ���� ��ǥ
    public static final String PLANT_GOAL_HUMIDITY = "goal_humidity";       //���� ���� ��ǥ
    public static final String PLANT_GOAL_LUX = "goal_lux";                 //���� ���� ��ǥ

    public static final String DATABASE_TABLE_MEASURED_DATA = "measured_data";
    //���� ������ ���̺�
    //public static final String PLANT_NUM = "plant_num";         //�Ĺ� ���� ��ȣ
    public static final String PLANT_MEASURED_TIME = "measured_time";   //���� �ð�
    public static final String PLANT_MEASURED_HUMIDITY = "humidity";    //���� ����
    public static final String PLANT_MEASURED_LUX = "lux";              //���� ����
    public static final String PLANT_MEASURED_TEMPERATURE = "temperature";  //���� �µ�

    //���� ����
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
            PLANT_GOAL_TEMPERATURE + " INTEGER, " +
            PLANT_GOAL_HUMIDITY + " INTEGER, "+
            PLANT_GOAL_LUX + " INTEGER " + ")";

    private static final String TABLE_CREATE_MEASURED_DATA = "CREATE TABLE "+
            DATABASE_TABLE_MEASURED_DATA + "(" +
            " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PLANT_NUM + " INTEGER, " +
            PLANT_MEASURED_TIME + " TEXT , " +
            PLANT_MEASURED_TEMPERATURE + " INTEGER, " +
            PLANT_MEASURED_HUMIDITY + "INTEGER, " +
            PLANT_MEASURED_LUX + " INTEGER " + ")";

    private SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mContext = null;

    //������
    public PlantDBAdapter(Context context)
    {
        this.mContext = context;
    }

    public PlantDBAdapter open() throws SQLException
    {
        mDBHelper = new DatabaseHelper(this.mContext);
        this.mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        mDBHelper.close();
    }

    //uuid ����̽� ����, ������ ������Ʈ �ð� ����
    public long createPhoneInfo(String uuid, int count, String time)
    {
        ContentValues initValues = new ContentValues();
        initValues.put(PHONE_UUID, uuid);
        initValues.put(DEVICE_NUMBER, count);
        initValues.put(UPDATE_TIME, time);
        return mDB.insert(DATABASE_TABLE_PHONE_INFO, null, initValues);
    }
    public long createNewPlant(PlantData plantData)
    {
        //����̽� �߰�
        ContentValues initValues = plantData.getContentValue();     //�������� ����� ����̽� ������
        return mDB.insert(DATABASE_TABLE_MONITOR_DEVICE_INFO, null, initValues);
    }
    public long createNewData(PlantData plantData)
    {
        long rs = 0;
        //������ ������ ���� ���� �ֱ�
        for(PlantData.MeasuredData data : plantData.mDataList)
        {
            ContentValues initValues = new ContentValues();
            initValues.put(PLANT_NUM, plantData.mPlantNum);
            initValues.put(PLANT_MEASURED_TIME, data.mTime);
            initValues.put(PLANT_MEASURED_HUMIDITY, data.mHumidity);
            initValues.put(PLANT_MEASURED_TEMPERATURE, data.mTemperature);
            initValues.put(PLANT_MEASURED_LUX, data.mLux);
            rs = mDB.insert(DATABASE_TABLE_MEASURED_DATA, null, initValues);
            if(rs == -1)
                return  -1;
        }
        return rs;
    }


    public long updateLastUpdatedTime(String time)
    {
        ContentValues updateValues = new ContentValues();
        updateValues.put(UPDATE_TIME, time);
        return mDB.update(DATABASE_TABLE_PHONE_INFO, updateValues, "_id = 1", null);
    }
    public long updatePlantInfo(PlantData plantData)
    {
        ContentValues updateValues = plantData.getContentValue();   //�������� ����� ����̽� ������
        return mDB.update(DATABASE_TABLE_MONITOR_DEVICE_INFO,       //db ������ �Ĺ� �ѹ��� ������ ������ ������Ʈ
                updateValues, PLANT_NUM + " = " + plantData.mPlantNum, null); //plant_num = 10
    }


    public long deletePhoneInfo(long rowID){
        return mDB.delete(DATABASE_TABLE_PHONE_INFO, null, null);   //��ü ����
    }
    public long deletePlantInfo(PlantData plantData)
    {
        return mDB.delete(DATABASE_TABLE_MONITOR_DEVICE_INFO, PLANT_NUM + " = " + plantData.mPlantNum, null) ;
    }
    public long deleteMesasuredData(String time, int plantNum)
    {
        //���� �ð��� �Ĺ� �ѹ��� �˻�
        return mDB.delete(DATABASE_TABLE_MEASURED_DATA, PLANT_NUM + " = " + plantNum + " and " + PLANT_MEASURED_TIME + " = " + time, null);
    }




    //Ŀ���� ���� Ŭ����
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //���̺� ����
            db.execSQL(TABLE_CREATE_PHONE_INFO);
            db.execSQL(TABLE_CREATE_MONITOR_DEVICE_INFO);
            db.execSQL(TABLE_CREATE_MEASURED_DATA);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //�ٸ� �������� ������ ���� ���̺��� ���� �� �� ���̺� ����
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_PHONE_INFO);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_MONITOR_DEVICE_INFO);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_MEASURED_DATA);

            onCreate(db);
        }
    }

}
