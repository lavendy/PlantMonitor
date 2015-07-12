package kr.ac.sch.cglab.plantmonitor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import kr.ac.sch.cglab.plantmonitor.Data.PhoneInfoManager;
import kr.ac.sch.cglab.plantmonitor.Data.PlantDBAdapter;
import kr.ac.sch.cglab.plantmonitor.Data.PlantData;
import kr.ac.sch.cglab.plantmonitor.Data.PlantsDataManager;
import kr.ac.sch.cglab.plantmonitor.NewPlant.AddPlantActivity;


public class MainActivity extends Activity implements View.OnTouchListener, AdapterView.OnItemClickListener {
    private Context mContext;

    private static PlantsDataManager mPlantManager;
    private ArrayList<PlantData> mDataList;     //식물 데이터 리스트

    private ListView mListViewPlantList;
    private Button mBtnAddNewPlant;

    private ListAdapterPlantList mListAdapterPlantList;

    public PlantDBAdapter mDBAdapter;

    //ble
    private BluetoothAdapter mBleAdapter;
    //private Handler mHandler;
    //private BluetoothAdapter.LeScanCallback mLeScanCallback;

    public MainActivity()
    {
    }

    public static void print(String str)
    {
        Log.d("log", str);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        InitData();
        InitGUI();
        InitBLE();
    }

    private void InitBLE()
    {

    }

    private void InitData()
    {
        mPlantManager = new PlantsDataManager();    //init
        mDataList = PlantsDataManager.mPlantsList;

        PlantsDataManager.mUUID = PhoneInfoManager.getUUID(this.mContext);   //setUUID;
        print("uuid: " + PlantsDataManager.mUUID);

        mDBAdapter = new PlantDBAdapter(this);
        try {
            mDBAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        //uuid 랑 초기 시간 입력
        mDBAdapter.createPhoneInfo(PlantsDataManager.mUUID, 0, PhoneInfoManager.getTime());
        print(mDBAdapter.getPhoneInfo());


        //text data
        PlantData data = new PlantData();
        data.mGoalHumidity = 60;
        data.mGoalTemperatureMin = 20;
        data.mGoalTemperatureMax = 25;
        data.mGoalLuxMin = 2000;
        data.mGoalLuxMax = 4000;

        data.mLastedHumidity = 80;
        data.mLastedLux = 8500;
        data.mLastedTemperature = 20;

        data.mPlantName = "TEST";
        data.mPlantNum = 0;

        mDataList.add(data);


        data = new PlantData();
        data.mGoalHumidity = 60;
        data.mGoalTemperatureMin = 20;
        data.mGoalTemperatureMax = 25;
        data.mGoalLuxMin = 2000;
        data.mGoalLuxMax = 4000;

        data.mLastedHumidity = 50;
        data.mLastedLux = 3000;
        data.mLastedTemperature = 15;

        data.mPlantName = "TEST";
        data.mPlantNum = 0;

        mDataList.add(data);
    }

    private void InitGUI()
    {
        //리스트뷰
        mListViewPlantList = (ListView) findViewById(R.id.activity_main_listview_plant_list);
        mListViewPlantList.setOnItemClickListener(this);

        //버튼
        mBtnAddNewPlant = (Button) findViewById(R.id.activity_main_btn_add_new_plant);
        mBtnAddNewPlant.setOnTouchListener(this);

        //생성
        mListAdapterPlantList = new ListAdapterPlantList(this, R.layout.list_adapter_plant_monitor, mDataList);
        mListViewPlantList.setAdapter(mListAdapterPlantList);   //set adapter
    }


    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if(v.getId() == R.id.activity_main_btn_add_new_plant && event.getAction() == MotionEvent.ACTION_UP)
        {
            Intent addPlantActivity = new Intent(MainActivity.this, AddPlantActivity.class);
            startActivity(addPlantActivity);
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

    }







}
