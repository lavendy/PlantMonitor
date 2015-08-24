package kr.ac.sch.cglab.plantmonitor.NewPlant;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
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

import kr.ac.sch.cglab.plantmonitor.R;

import com.google.android.gms.plus.PlusOneButton;

import java.util.ArrayList;
import java.util.List;

public class AddPlantDeviceFragment0 extends Fragment implements View.OnTouchListener, AdapterView.OnItemClickListener
{
    public static final String TAG = AddPlantDeviceFragment0.class.getSimpleName();

    private static final short REQUEST_ENABLE_BT = 1;
    private static final long SCAN_TIMEOUT = 4000;

    //reference variable p0
    private ListView mListViewDeviceList;
    private Button mBtnScanDevice;
    private TextView mTextViewStatus;
    private ProgressBar mProgressBarScanStatus;
    private ListAdapterScanDevice mDeviceListAdapter;
    private Button mBtnBack;

    private ArrayList<ScannedBluetoothDevice> mScannedDeviceList;   //스캔된 ble 디바이스들 보관 리스트
    private ScannedBluetoothDevice mSelectDevice;                   //선택된 블루투스 디바이스 저장

    private BluetoothAdapter mBleAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;

    private Handler mHandler;
    private boolean mIsScanning = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.mHandler = new Handler();
        mScannedDeviceList = new ArrayList<ScannedBluetoothDevice>();

        View v = inflater.inflate(R.layout.activity_add_plant_p0, container, false);

        mListViewDeviceList = (ListView) v.findViewById(R.id.activity_add_plant_p0_listView_searched);
        mBtnScanDevice = (Button) v.findViewById(R.id.activity_add_plant_p0_btn_search_device);
        mTextViewStatus = (TextView) v.findViewById(R.id.activity_add_plant_p0_textView_status);
        mProgressBarScanStatus = (ProgressBar) v.findViewById(R.id.activity_add_plant_p0_progressBar_search);
        mBtnBack = (Button) v.findViewById(R.id.activity_add_plant_p0_btn_back);

        //p0 ui 초기화
        this.mDeviceListAdapter = new ListAdapterScanDevice(getActivity(), R.layout.list_adapter_new_device, this.mScannedDeviceList);
        this.mListViewDeviceList.setAdapter(this.mDeviceListAdapter);
        this.mListViewDeviceList.setOnItemClickListener(this);
        this.mBtnScanDevice.setOnTouchListener(this);
        this.mBtnBack.setOnTouchListener(this);

        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if( !checkEnableBluetooth()){    //사용불가 종료
            getActivity().finish();
        }
        InitializeBle();    //ble 초기화
    }

    private boolean checkEnableBluetooth()
    {
        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
        {
            Toast.makeText(getActivity(), "블루투스를 사용 할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    void InitializeBle()
    {
        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(getActivity().BLUETOOTH_SERVICE);
        this.mBleAdapter = bluetoothManager.getAdapter();

        if(this.mBleAdapter == null || !this.mBleAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        //registration of callback
        this.mLeScanCallback = new BluetoothAdapter.LeScanCallback()
        {
            @Override
            public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord)
            {
                Log.d("tes", "device found: " + device.getName());

                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ScannedBluetoothDevice newDevice = new ScannedBluetoothDevice(device, rssi);
                        mDeviceListAdapter.addDevice(newDevice);
                        mDeviceListAdapter.notifyDataSetChanged();
                    }
                });
            }
        };

        //처음 스캔 시작
        scanLeDevice(true);
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

        this.mTextViewStatus.setText("" + this.mScannedDeviceList.size() + "개 디바이스 검색됨");
        this.mProgressBarScanStatus.setVisibility(View.INVISIBLE);
    }

    private void startDeviceScan()
    {
        mBleAdapter.startLeScan(mLeScanCallback);

        this.mIsScanning = true;    //스캥 상태임 중복실행 방지

        getActivity().runOnUiThread(new Runnable() {      //리스트 뷰 초기화 하고
            @Override
            public void run() {
                mScannedDeviceList.clear();         //디바이스 리스트 초기화
                mDeviceListAdapter.notifyDataSetChanged();

                //gui 업데이트
                mTextViewStatus.setText("디바이스를 찾는중...");
                mProgressBarScanStatus.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onStop()
    {
        scanLeDevice(false);

        super.onStop();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP)
        {
            if(v.getId() == R.id.activity_add_plant_p0_btn_search_device)
            {
                scanLeDevice(true); //ble device 스캔 시작
            }
            else if(v.getId() == R.id.activity_add_plant_p0_btn_back)
            {
                ((AddPlantActivity)getActivity()).moveToPreviousPage(); //이전 페이지 이동
            }
        }

        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        scanLeDevice(false);                                                                    //블루투스 스캔 스탑
        mSelectDevice = (ScannedBluetoothDevice) mDeviceListAdapter.getItem(position);        //선택된 디바이스 정보 저장

        //broadcast to addplantActivity     //다음 페이지로 이동
        ((AddPlantActivity)getActivity()).moveToFragment1(mSelectDevice.mDevice);   //상위 activity 의 다음 fragment 교체 메쏘드 요청
    }
}
