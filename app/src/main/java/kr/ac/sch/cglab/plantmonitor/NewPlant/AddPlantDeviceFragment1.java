package kr.ac.sch.cglab.plantmonitor.NewPlant;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import kr.ac.sch.cglab.plantmonitor.BLE.DeviceData;
import kr.ac.sch.cglab.plantmonitor.BLE.GattAttributes;
import kr.ac.sch.cglab.plantmonitor.Data.PlantData;
import kr.ac.sch.cglab.plantmonitor.Data.PlantsDataManager;
import kr.ac.sch.cglab.plantmonitor.R;

/**
 * Created by Administrator on 2015-08-08.
 */
public class AddPlantDeviceFragment1 extends Fragment implements View.OnTouchListener
{
    private static final String TAG = AddPlantDeviceFragment1.class.getSimpleName();

    private ImageButton mImgBtnSelectPlant;
    private EditText mEditTextDeviceName;
    private EditText mEditTextPlantName;
    private EditText mEditTextCurrTemperature;
    private EditText mEditTextCurrHumidity;
    private EditText mEditTextCurrLux;

    private Button mBtnBack;
    private Button mBtnConfirm;

    private ProgressDialog mDialogStoreDB;

    //ble variable
    private BluetoothGatt mBleGatt = null;
    private BluetoothDevice mDevice = null;

    private DeviceData mCurrentDeviceData = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.activity_add_plant_p1, container, false);

        this.mImgBtnSelectPlant       = (ImageButton) v.findViewById(R.id.activity_add_plant_p1_btn_add_plant);
        this.mEditTextPlantName       = (EditText)    v.findViewById(R.id.activity_add_plant_p1_editBox_plant_name);
        this.mEditTextDeviceName      = (EditText)    v.findViewById(R.id.activity_add_plant_p1_editBox_device_name);
        this.mEditTextCurrTemperature = (EditText)    v.findViewById(R.id.activity_add_plant_p1_text_temperature);
        this.mEditTextCurrHumidity    = (EditText)    v.findViewById(R.id.activity_add_plant_p1_text_humidity);
        this.mEditTextCurrLux         = (EditText)    v.findViewById(R.id.activity_add_plant_p1_text_lux);

        //this.mProgressTemperature     = (ProgressBar) v.findViewById(R.id.activity_add_plant_p1_editBox_plant_name)

        this.mBtnBack                 = (Button)      v.findViewById(R.id.activity_add_plant_p1_btn_back);
        this.mBtnConfirm              = (Button)      v.findViewById(R.id.activity_add_plant_p1_btn_commit);

        this.mImgBtnSelectPlant.setOnTouchListener(this);
        this.mBtnConfirm.setOnTouchListener(this);
        this.mBtnBack.setOnTouchListener(this);

        this.mEditTextCurrTemperature.setEnabled(false);
        this.mEditTextCurrHumidity.setEnabled(false);
        this.mEditTextCurrLux.setEnabled(false);

        //store current data
        this.mDialogStoreDB = new ProgressDialog(getActivity());
        this.mDialogStoreDB.setMessage("식물 정보를 등록 하고 있습니다.");
        this.mDialogStoreDB.setCancelable(false);

        this.mCurrentDeviceData = new DeviceData();

        return v;
    }

    public void connectBLE(BluetoothDevice device)
    {
        Log.d(TAG, "connectBLE");

        this.mDevice = device;
        this.mBleGatt = this.mDevice.connectGatt(getActivity(), false, mGattCallback); //connect gatt service

        if(mBleGatt == null)    //연결 실패
        {
            Toast.makeText(getActivity(), this.mDevice.getName() +" 디바이스 Gatt 서버에 연결을 할 수 없습니다.", Toast.LENGTH_SHORT).show();
            ((AddPlantActivity)getActivity()).moveToPreviousPage(); //뒷 페이지로 이동
        }
    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
    {
        @Override
        //connectgatt 으로 연결 되면 아래 함수 호출
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
            switch (newState)
            {
                case BluetoothProfile.STATE_CONNECTED: //정상 연결
                    //ble 가 연결 됬으면 우선 대기 자동으로 service discovered 호출
                    Log.d(TAG, "gatt connected");
                    mBleGatt.discoverServices();   //find characteristic

                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.d(TAG, "gatt disconnected");
                    break;
            }
        }

        //discoverServices 로 서비스가 발견 됬으면 아래 호출
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            Log.d(TAG, "onServicesDiscovered");
            if(status == BluetoothGatt.GATT_SUCCESS)
            {
                List<BluetoothGattService> services = mBleGatt.getServices();

                BluetoothGattService sensingService = mBleGatt.getService(UUID.fromString(GattAttributes.SENSING_SERVICE));
                //Log.d(TAG, "" + sensingService.getUuid());

                if(sensingService == null)
                {
                    Log.d(TAG, "Not found service : Sensing service");
                    return;
                }

                BluetoothGattCharacteristic sensingDataCharacteristic = sensingService.getCharacteristic(UUID.fromString(GattAttributes.SENSING_DATA_MEASUREMENT));

                if(sensingDataCharacteristic == null)
                {
                    Log.d(TAG, "Not found characteristic : Data Measurement");
                    return;
                }

                //mBleGatt.readCharacteristic(sensingDataCharacteristic);
                boolean notificationRegistered = mBleGatt.setCharacteristicNotification(sensingDataCharacteristic, true);

                if(notificationRegistered){
                    BluetoothGattDescriptor descriptor = sensingDataCharacteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBleGatt.writeDescriptor(descriptor);

                    Log.d(TAG,"set notification success");
                }
                else
                {
                    Log.d(TAG, "Can not setup the notification");
                    return;
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            //Log.d(TAG, "onCharacteristicChanged");
            if(characteristic.getUuid().equals(UUID.fromString(GattAttributes.SENSING_DATA_MEASUREMENT)))
            {
                //데이토 포맷 / 오프셋 sint16 = 2바이트 단위 = 2씩 오프셋 들어감
                int lux = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 0);    //온도 습도 조도
                int tmp = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 2);    //온도 습도 조도
                int hu = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 4);    //온도 습도 조도
                Log.d(TAG,"notifi data : " + lux + " | "+ tmp + " | " + hu);

                //update ui information
                mCurrentDeviceData.mDevice = mDevice;
                mCurrentDeviceData.mCurrentTemperature = tmp;
                mCurrentDeviceData.mCurrentHumidity = hu;
                mCurrentDeviceData.mCurrentLux = lux;
                updateGUIInformation(mCurrentDeviceData);

            }
        }
    };


    private void updateGUIInformation(final DeviceData updateData)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEditTextDeviceName.setText(updateData.mDevice.getName());            //디바이스 이름
                mEditTextCurrTemperature.setText("" + updateData.mCurrentTemperature);     //온도
                mEditTextCurrHumidity.setText("" + updateData.mCurrentHumidity);           //습도
                mEditTextCurrLux.setText("" + updateData.mCurrentLux);                     //조도 정보 갱신
            }
        });
    }


    @Override
    public void onDestroy()
    {
        if(mBleGatt != null)
            mBleGatt.close();
        mBleGatt = null;

        super.onDestroy();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_UP)
        {
            if(v.getId() == R.id.activity_add_plant_p1_btn_add_plant)   //이미지 등록 버튼 클릭
            {

            }
            else if(v.getId() == R.id.activity_add_plant_p1_btn_back)
            {
                ((AddPlantActivity)getActivity()).moveToPreviousPage();
            }
            else if(v.getId() == R.id.activity_add_plant_p1_btn_commit)
            {
                PlantData addPlant = updateNewDeviceInformation();   //데이터들을 객체에 저장

                if(storeDeviceToDB(addPlant) == true)   //등록 성공 하였으면 3페이지 이동
                {
                    updateNewPlantDataToBleDevice(addPlant);
                    ((AddPlantActivity)getActivity()).moveToFragment2(addPlant);
                }
            }
        }

        return false;
    }



    private PlantData updateNewDeviceInformation()
    {
        PlantData newPlant = new PlantData();

        newPlant.mImgNum = 0;
        newPlant.mPlantNum = 1;
        newPlant.mPlantName = ""+mEditTextPlantName.getText();

        newPlant.mDevice = this.mDevice;

        newPlant.mGoalTemperatureMin = 15;
        newPlant.mGoalTemperatureMax = 18;
        newPlant.mGoalHumidity = 50;
        newPlant.mGoalLuxMin = 700;
        newPlant.mGoalLuxMax = 1000;

        return newPlant;
    }

    //DB에 디바이스 저장
    private boolean storeDeviceToDB(PlantData data)
    {
        boolean success = false;

        this.mDialogStoreDB.show();

        long rs = PlantsDataManager.mPlantDBAdapter.createNewPlant(data);
        if(rs != -1)    //디비 등록 성공
        {
            PlantsDataManager.mPlantDBAdapter.addNewPlantDeviceToPhoneInfo();   //DB device 개수 업데이트
            PlantsDataManager.mPlantsList.add(data);                            //리스트에 디바이스 추가

            success = true;
        }

        this.mDialogStoreDB.dismiss();

        return success;
    }

    //ble 디바이스에 새로 추가된 식물 정보와 제어 정보 전송
    private void updateNewPlantDataToBleDevice(PlantData addPlant)
    {

    }

}
