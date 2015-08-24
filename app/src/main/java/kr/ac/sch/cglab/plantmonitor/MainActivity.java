package kr.ac.sch.cglab.plantmonitor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;

import kr.ac.sch.cglab.plantmonitor.BLE.BluetoothLeService;
import kr.ac.sch.cglab.plantmonitor.Data.PhoneInfoManager;
import kr.ac.sch.cglab.plantmonitor.Data.PlantDBAdapter;
import kr.ac.sch.cglab.plantmonitor.Data.PlantData;
import kr.ac.sch.cglab.plantmonitor.Data.PlantsDataManager;
import kr.ac.sch.cglab.plantmonitor.NewPlant.AddPlantActivity;


public class MainActivity extends Activity implements View.OnTouchListener, AdapterView.OnItemClickListener {
    private final String TAG = MainActivity.class.getSimpleName();

    private Context mContext;

    private static PlantsDataManager mPlantManager;
    private ArrayList<PlantData> mDataList;     //식물 데이터 리스트

    private ListView mListViewPlantList;
    private Button mBtnAddNewPlant;

    private ListAdapterPlantList mListAdapterPlantList;

    public PlantDBAdapter mDBAdapter;

    //ble
    private BluetoothAdapter mBleAdapter;
    private BluetoothLeService mBluetoothLeService = null;
    private Handler mHandler;

    private final ServiceConnection mServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder)service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            mBluetoothLeService = null;
        }
    };

    //데이터 처리
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)   //넘겨 받을때 무조건 mac address 검사
        {
            final String action = intent.getAction();
            String address = intent.getStringExtra("ID");   //address 추출

            PlantData plantData = PlantsDataManager.getPlantData(address);  //검색
            int plantIndex = PlantsDataManager.getPlantDataIndex(address);  //순번 검색

            //id 검색후
            if(action == BluetoothLeService.ACTION_GATT_CONNECTED)  //ble 연결 되었으면
            {
                plantData.mBleGatt.discoverServices();
            }
            else if(action == BluetoothLeService.ACTION_GATT_DISCONNECTED)
            {

            }
            else if(action == BluetoothLeService.ACTION_GATT_SERVICE_DISCOVERED)
            {

            }
            else if(action == BluetoothLeService.ACTION_DATA_AVAILABLE) //read
            {

            }
            else if(action == BluetoothLeService.ACTION_DATA_SENSING_NOTIFICATION)  //lux temp, humi
            {

            }
            else if(action == BluetoothLeService.ACTION_DATA_ALERT_STATUS_NOTIFICATION) //경고용 알림
            {

            }
        }
    };


    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
        {
            //검색된 ble 디바이스중에서 등록된 디바이스가 존재 하면 ble 연결 리스트에
            for (PlantData plantDevice : mDataList)
            {
                if(plantDevice.mDevice.getAddress() == device.getAddress())
                {

                }
            }
        }
    };

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

        mHandler = new Handler();

        InitBLE();
        InitData();
        InitGUI();

        //서비스 등록
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }

    private void InitBLE()
    {

    }

    private void InitData()
    {
        mPlantManager = new PlantsDataManager();    //init

        mDataList = new ArrayList<PlantData>();     //디바이스 관리 리스트
        PlantsDataManager.mPlantsList = mDataList;

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
        PlantsDataManager.mPlantDBAdapter = mDBAdapter; //글로벌 변수 등록

        mDBAdapter.createPhoneInfo(PlantsDataManager.mUUID, 0, PhoneInfoManager.getTime());
        print(mDBAdapter.getPhoneInfo());

    }

    private void updateDeviceList()
    {
        print("updateDeviceList");

        ArrayList<PlantData> list = mDBAdapter.getPlantDeviceListFromDB();  //DB에서 디바이스 정보 읽어 들이기

        if(list != null)
        {
            for (PlantData data : list)
            {
                mDataList.add(data);
            }
        }
    }

    private void connectDeviceList()
    {
        if(mBluetoothLeService != null)
        {
            for (PlantData plantDevice : mDataList)
            {
                final boolean result = mBluetoothLeService.connect(plantDevice);  //디바이스 연결
                Log.d(TAG, "Connect request result=" + result);
            }
        }
    }

    @Override
    protected void onResume()
    {
        print("onResume");
        super.onResume();

        mDataList.clear();
        updateDeviceList(); //리스트 초기화 후 다시 재 갱신 //추가되는 디바이스가 존재 할 수 있으므로

        //디바이스 연결 시도
        connectDeviceList();

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
