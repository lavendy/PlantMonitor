package kr.ac.sch.cglab.plantmonitor.NewPlant;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kr.ac.sch.cglab.plantmonitor.Data.BluetoothLEHelpUtil;
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

    //스캔된 ble 디바이스 보관
    private ArrayList<ScannedBluetoothDevice> mScannedDeviceList;

    //ble 관련
    private BluetoothAdapter mBleAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private BluetoothGatt mBleGatt;
    //private BluetoothGattCallback mBleGattCallback; 밑에서 정의해줌
    private Handler mHandler;
    boolean mIsScanning = false;

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

        //init reference
        this.mP0ListViewDeviceList      = (ListView)    this.mPageViews.get(VIEW_PAGE_P1).findViewById(R.id.activity_add_plant_p0_listView_searched);
        this.mP0BtnScanDevice           = (Button)      this.mPageViews.get(VIEW_PAGE_P1).findViewById(R.id.activity_add_plant_p0_btn_search_device);
        this.mP0TextViewStatus          = (TextView)    this.mPageViews.get(VIEW_PAGE_P1).findViewById(R.id.activity_add_plant_p0_textView_status);
        this.mP0ProgressBarScanStatus   = (ProgressBar) this.mPageViews.get(VIEW_PAGE_P1).findViewById(R.id.activity_add_plant_p0_progressBar_search);

        //디바이스 리스트
        this.mScannedDeviceList = new ArrayList<ScannedBluetoothDevice>();

        //p0 ui 초기화
        this.mP0DeviceListAdapter = new ListAdapterScanDevice(this, R.layout.list_adapter_new_device, this.mScannedDeviceList);
        this.mP0ListViewDeviceList.setAdapter(this.mP0DeviceListAdapter);
        this.mP0ListViewDeviceList.setOnItemClickListener(this);
        this.mP0BtnScanDevice.setOnTouchListener(this);


        //우측 상단 종료 버튼
        this.mViewGroup.findViewById(R.id.activity_add_plant_viewpager_btn_exit).setOnTouchListener(this);

        setContentView(this.mViewGroup);

        InitBle();
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
}
