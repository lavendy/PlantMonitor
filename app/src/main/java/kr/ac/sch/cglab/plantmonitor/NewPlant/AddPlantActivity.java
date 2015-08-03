package kr.ac.sch.cglab.plantmonitor.NewPlant;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

import kr.ac.sch.cglab.plantmonitor.BLE.GattAttributes;
import kr.ac.sch.cglab.plantmonitor.R;


public class AddPlantActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, View.OnTouchListener {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_TIMEOUT = 10000;  //ble 검색 시간

    private static final int VIEW_PAGE_P1 = 0;
    private static final int VIEW_PAGE_P2 = 1;
    private static final int VIEW_PAGE_P3 = 2;


    private ViewPager mViewPager;
    private ArrayList<View> mPageViews;
    private ViewGroup mViewGroup;

    //reference variable p0
    private ListView mP0ListViewDeviceList;
    private Button mP0BtnScanDevice;
    private TextView mP0TextViewStatus;
    private ProgressBar mP0ProgressBarScanStatus;
    private ListAdapterScanDevice mP0DeviceListAdapter;

    //p1
    private ImageButton mP1ImgBtnSelectPlant;
    private EditText mP1EditTextDeviceName;
    private EditText mP1EditTextPlantName;
    private EditText mP1EditTextCurrTemperature;
    private EditText mP1EditTextCurrHumidity;
    private EditText mP1EditTextCurrLux;
    private Button mP1BtnConfirm;

    private ScannedBluetoothDevice mSelectDevice; //선택된 블루투스 디바이스 임시 저장

    //스캔된 ble 디바이스 보관
    private ArrayList<ScannedBluetoothDevice> mScannedDeviceList;

    //ble 관련
    private BluetoothAdapter mBleAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private BluetoothGatt mBleGatt;
    //private BluetoothGattCallback mBleGattCallback; 밑에서 정의해줌
    private Handler mHandler;
    private boolean mIsScanning = false;

    private int mBleStatus;

    private BluetoothGattCharacteristic ch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getLayoutInflater();
        this.mPageViews = new ArrayList<View>();
        this.mPageViews.add(inflater.inflate(R.layout.activity_add_plant_p0, null));
        this.mPageViews.add(inflater.inflate(R.layout.activity_add_plant_p1, null));
        this.mPageViews.add(inflater.inflate(R.layout.activity_add_plant_p2, null));

        this.mViewGroup = (ViewGroup) inflater.inflate(R.layout.activity_add_plant_viewpager, null);

        this.mViewPager = (ViewPager) this.mViewGroup.findViewById(R.id.activity_add_plant_viewpager_pages);
        this.mViewPager.setAdapter(new ViewPagerAdapterAddPlant(this.mPageViews, this));

        //디바이스 리스트
        this.mScannedDeviceList = new ArrayList<ScannedBluetoothDevice>();

        InitGUI();

        setContentView(this.mViewGroup);

        InitBle();
    }

    private void InitGUI()
    {
        //우측 상단 종료 버튼
        this.mViewGroup.findViewById(R.id.activity_add_plant_viewpager_btn_exit).setOnTouchListener(this);

        //init reference p0
        this.mP0ListViewDeviceList      = (ListView)    this.mPageViews.get(VIEW_PAGE_P1).findViewById(R.id.activity_add_plant_p0_listView_searched);
        this.mP0BtnScanDevice           = (Button)      this.mPageViews.get(VIEW_PAGE_P1).findViewById(R.id.activity_add_plant_p0_btn_search_device);
        this.mP0TextViewStatus          = (TextView)    this.mPageViews.get(VIEW_PAGE_P1).findViewById(R.id.activity_add_plant_p0_textView_status);
        this.mP0ProgressBarScanStatus   = (ProgressBar) this.mPageViews.get(VIEW_PAGE_P1).findViewById(R.id.activity_add_plant_p0_progressBar_search);

        //p0 ui 초기화
        this.mP0DeviceListAdapter = new ListAdapterScanDevice(this, R.layout.list_adapter_new_device, this.mScannedDeviceList);
        this.mP0ListViewDeviceList.setAdapter(this.mP0DeviceListAdapter);
        this.mP0ListViewDeviceList.setOnItemClickListener(this);
        this.mP0BtnScanDevice.setOnTouchListener(this);

        //page2
        this.mP1ImgBtnSelectPlant       = (ImageButton) this.mPageViews.get(VIEW_PAGE_P2).findViewById(R.id.activity_add_plant_p1_btn_add_plant);
        this.mP1EditTextDeviceName      = (EditText)    this.mPageViews.get(VIEW_PAGE_P2).findViewById(R.id.activity_add_plant_p1_editBox_device_name);
        this.mP1EditTextCurrTemperature = (EditText)    this.mPageViews.get(VIEW_PAGE_P2).findViewById(R.id.activity_add_plant_p1_text_temperature);
        this.mP1EditTextCurrHumidity    = (EditText)    this.mPageViews.get(VIEW_PAGE_P2).findViewById(R.id.activity_add_plant_p1_text_humidity);
        this.mP1EditTextCurrLux         = (EditText)    this.mPageViews.get(VIEW_PAGE_P2).findViewById(R.id.activity_add_plant_p1_text_lux);
        this.mP1BtnConfirm              = (Button)      this.mPageViews.get(VIEW_PAGE_P2).findViewById(R.id.activity_add_plant_p1_btn_registration);

        this.mP1ImgBtnSelectPlant.setOnTouchListener(this);
        this.mP1BtnConfirm.setOnTouchListener(this);
        this.mP1EditTextCurrTemperature.setEnabled(false);
        this.mP1EditTextCurrHumidity.setEnabled(false);
        this.mP1EditTextCurrLux.setEnabled(false);

    }

    //ble part
    private void InitBle()
    {
        checkEnableBluetooth();

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        this.mBleAdapter = bluetoothManager.getAdapter();

        if(this.mBleAdapter == null || !this.mBleAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        this.mHandler = new Handler();

        //registration of callback
        this.mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord)
            {
                Log.d("tes", "device found: " + device.getName());
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ScannedBluetoothDevice newDevice = new ScannedBluetoothDevice(device, rssi);
                        mP0DeviceListAdapter.addDevice(newDevice);
                        mP0DeviceListAdapter.notifyDataSetChanged();

                        //BluetoothLEHelpUtil.printScanRecord(scanRecord);
                    }
                });
            }
        };

        //처음 스캔 시작
        scanLeDevice(true);
    }

    //gatt 서비스 정의
    private BluetoothGattCallback mBleGattCallback = new BluetoothGattCallback() {
        @Override
        //connectgatt 으로 연결 되면 아래 함수 호출
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
             switch (newState)
             {
                 case BluetoothProfile.STATE_CONNECTED: //정상 연결
                     gatt.discoverServices();           //서비스 검색
                     mBleStatus = newState;
                     break;
                 case BluetoothProfile.STATE_DISCONNECTED:
                     mBleStatus = newState;
                     break;
             }
        }

        //discoverServices 로 서비스가 발견 됬으면 아래 호출
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            if(status == BluetoothGatt.GATT_SUCCESS)
            {
                print("onServicesDiscovered");

                BluetoothGattService s = gatt.getService(GattAttributes.UUID_SERVICE_SENSING);
                print(" ss "+s.getUuid());

                ch = s.getCharacteristic(GattAttributes.UUID_CHARACTERISTIC_SENSING_DATA);
                print(" cc "+ch.getUuid());

                boolean registered = gatt.setCharacteristicNotification(ch, true);

                print("111");
                //BluetoothGattDescriptor descriptor = ch.getDescriptor(UUID.fromString(DESCRIPTOR_CHAR_LUMINISITY));
                //descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                //gatt.writeDescriptor(descriptor);
                print("222");
                if(registered){
                    print("success");
                }
                else
                    print("nop");
            }

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            print("onCharacteristicChanged");
            if (GattAttributes.UUID_CHARACTERISTIC_SENSING_DATA.equals(characteristic.getUuid())) {
                int value = characteristic.getValue()[0];
                print("lux = " + value);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            print("test");
            if(status == BluetoothGatt.GATT_SUCCESS)
            {
                print("test");
                print("test" + characteristic.getUuid().toString());
                if(GattAttributes.UUID_CHARACTERISTIC_SENSING_DATA.equals(characteristic.getUuid()))
                {
                    print("test");
                    final String lux = characteristic.getStringValue(0);
                    print("lux : "+lux);
                }
            }
        }
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }
    };


    private void checkEnableBluetooth()
    {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "블루투스를 사용 할 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    //ble 스캔 시작 제어
    private  void scanLeDevice(final boolean enable)
    {
        if(enable == true && this.mIsScanning == false) //이전에 스캔이 안끝났으면 ㅁ시
        {
            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    stopDeviceScan();
                }
            }, SCAN_TIMEOUT);   //timeout 시간 후 스캔 중지

            startDeviceScan();
        }
        else if(enable == false)
        {
            stopDeviceScan();
        }
    }

    private void stopDeviceScan()
    {
        mBleAdapter.stopLeScan(mLeScanCallback);

        this.mIsScanning = false;  //스캔 상태 중복 장비

        this.mP0TextViewStatus.setText("" + this.mScannedDeviceList.size() + "개 디바이스 검색됨");
        this.mP0ProgressBarScanStatus.setVisibility(View.INVISIBLE);
    }
    private void startDeviceScan()
    {
        mBleAdapter.startLeScan(mLeScanCallback);

        this.mIsScanning = true;    //스캥 상태임 중복실행 방지

        runOnUiThread(new Runnable() {      //리스트 뷰 초기화 하고
            @Override
            public void run() {
                mScannedDeviceList.clear();         //디바이스 리스트 초기화
                mP0DeviceListAdapter.notifyDataSetChanged();

                //gui 업데이트
                mP0TextViewStatus.setText("디바이스를 찾는중...");
                mP0ProgressBarScanStatus.setVisibility(View.VISIBLE);
            }
        });
    }
    //ble end


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(position != -1)
        {
            scanLeDevice(false);                                                                    //블루투스 스캔 스탑
            mSelectDevice = (ScannedBluetoothDevice) mP0DeviceListAdapter.getItem(position);        //선택된 디바이스 정보 저장
            this.mBleGatt = mSelectDevice.mDevice.connectGatt(this, false, this.mBleGattCallback);  //gatt 서버에 연결
            moveToNextPage();   //다음 페이지로 이동

        }
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //우측 상단 누르면 plant 등록 액티비티 종료
        if( event.getAction() == MotionEvent.ACTION_UP){
            if(v.getId() == R.id.activity_add_plant_viewpager_btn_exit) //우측 상단 x 버튼 누르면 종료
            {
                finish();
                return true;
            }
            else if(v.getId() == R.id.activity_add_plant_p0_btn_search_device)  //page1 스캔 버튼 누르면
            {
                scanLeDevice(true); //ble device 스캔 시작
            }

            //page 2
            else if(v.getId() == R.id.activity_add_plant_p1_btn_add_plant)      //식물 선택 창으로 전환
            {

            }
            else if(v.getId() == R.id.activity_add_plant_p1_btn_registration)   //페이지 정보 입력 완료 p3 이동
            {
                print("sdgdsgs");
                mBleGatt.readCharacteristic(ch);
            }

        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            moveToPreviousPage();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        this.mBleGatt.disconnect(); //갓 서비스 종료
        scanLeDevice(false);        //스캔 종료

        super.onDestroy();
    }

    private void moveToPreviousPage(){
        if(this.mViewPager.getCurrentItem() == 0){    //맨처음 페이지에서 뒤로 누르면 액티비티 종료
            finish();
        }
        else{
            this.mViewPager.setCurrentItem(this.mViewPager.getCurrentItem() - 1);
        }
    }
    private void moveToNextPage(){
        this.mViewPager.setCurrentItem(this.mViewPager.getCurrentItem()+1);     //다음 페이지 이동
    }

    private void print(String str)
    {
        Log.v("pm", str);
    }
}
