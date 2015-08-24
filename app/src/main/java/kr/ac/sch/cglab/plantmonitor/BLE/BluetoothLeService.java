package kr.ac.sch.cglab.plantmonitor.BLE;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

import kr.ac.sch.cglab.plantmonitor.Data.PlantData;
import kr.ac.sch.cglab.plantmonitor.Data.PlantsDataManager;

/**
 * Created by Administrator on 2015-08-03.
 */
public class BluetoothLeService extends Service {
    public static BluetoothLeService LocalBinder;



    class BleGattData{
        public BluetoothGatt mBleGatt = null;
        public BluetoothDevice mDevice = null;
    };

    private static final String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager    mBluetoothManager;
    private BluetoothAdapter    mBluetoothAdapter;

    private BluetoothGatt       mBluetoothGatt;
    private ArrayList<BleGattData> mGattList;

    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED                    = "kr.ac.sch.cglab.plantmonitor.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED                 = "kr.ac.sch.cglab.plantmonitor.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICE_DISCOVERED           = "kr.ac.sch.cglab.plantmonitor.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_SENSING_NOTIFICATION         = "kr.ac.sch.cglab.plantmonitor.ACTION_GATT_NOTIFICATION";  //lux, temp,
    public final static String ACTION_DATA_ALERT_STATUS_NOTIFICATION    = "kr.ac.sch.cglab.plantmonitor.ACTION_DATA_ALERT_STATUS";  //설정 값 알림 characteristic
    public final static String ACTION_DATA_AVAILABLE                    = "kr.ac.sch.cglab.plantmonitor.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA                               = "kr.ac.sch.cglab.plantmonitor.EXTRA_DATA";

    private  BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
    {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
            String address = gatt.getDevice().getAddress();

            String intentAction;
            if(newState == BluetoothProfile.STATE_CONNECTED)
            {
                intentAction = ACTION_GATT_CONNECTED;   //"kr.ac.sch.cglab.plantmonitor.ACTION_GATT_CONNECTED";
                broadcastUpdate(intentAction, address);

                Log.d(TAG, "Connected to GATT server");

            }
            else if(newState == BluetoothProfile.STATE_DISCONNECTED)
            {
                intentAction = ACTION_GATT_DISCONNECTED;
                broadcastUpdate(intentAction, address);
            }
        }
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            String address = gatt.getDevice().getAddress();
            if(status == BluetoothGatt.GATT_SUCCESS)
            {
                broadcastUpdate(ACTION_GATT_SERVICE_DISCOVERED, address);
            }
            else
            {
                Log.d(TAG, "onServicedDiscovered received : " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            String address = gatt.getDevice().getAddress();
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic, address);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
        {
            String address = gatt.getDevice().getAddress();

            //서비스 종료 검사
            if(characteristic.getUuid() == UUID.fromString(GattAttributes.SENSING_DATA_MEASUREMENT))
            {
                broadcastUpdate(ACTION_DATA_SENSING_NOTIFICATION, characteristic, address);        //센싱 알림

            }
            else if(characteristic.getUuid() == UUID.fromString(GattAttributes.SENSING_DATA_MEASUREMENT))
            {
                broadcastUpdate(ACTION_DATA_ALERT_STATUS_NOTIFICATION, characteristic, address);   //설정치 경고 알림
            }

        }
    };


    private void broadcastUpdate(String intentAction, String id) {
        final Intent intent = new Intent(intentAction);
        intent.putExtra("ID", id);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(String intentAction, BluetoothGattCharacteristic characteristic, String id) {
        final Intent intent = new Intent(intentAction);
        intent.putExtra("ID", id);

        //조도 습도 온도 데이터 처리
        if(characteristic.getUuid() == UUID.fromString(GattAttributes.SENSING_DATA_MEASUREMENT))
        {
            //데이토 포맷 / 오프셋 sint16 = 2바이트 단위 = 2씩 오프셋 들어감
            int lux = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 0);    //온도 습도 조도
            int tmp = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 2);    //온도 습도 조도
            int hu = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 4);    //온도 습도 조도
            Log.d(TAG,"notifi data : " + lux + " | "+ tmp + " | " + hu);

            intent.putExtra("LUX", lux);
            intent.putExtra("TEMPERATURE", tmp);
            intent.putExtra("HUMIDITY", hu);
        }

        sendBroadcast(intent);
    }


    public boolean initialize()
    {
        if(mBluetoothManager == null)
        {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if(mBluetoothManager == null)
            {
                if (mBluetoothManager == null)
                {
                    Log.e(TAG, "Unable to initialize BluetoothManager.");
                    return false;
                }
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null)
        {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    //connects to the GATT server
    public boolean connect(PlantData plantDevice)
    {
        if(mBluetoothAdapter == null || plantDevice == null)
        {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        String address = plantDevice.getAddress();

        if(plantDevice.mDevice != null)
        {
            plantDevice.mBleGatt.connect();
            return true;
        }

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        if(device == null)
        {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        else
            plantDevice.mDevice = device;

        plantDevice.mBleGatt = plantDevice.mDevice.connectGatt(this, false, mGattCallback);


        return true;
    }

    public void disconnect()
    {
        if(mBluetoothAdapter == null || mBluetoothGatt == null)
        {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }
    public void discoonect(PlantData data)
    {
        data.mBleGatt.disconnect();
    }

    public void close()
    {
        if(mBluetoothGatt == null)
        {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
    public void close(PlantData data)
    {
        data.mBleGatt.close();
        data.mBleGatt = null;
    }


    public class LocalBinder extends Binder
    {
        public BluetoothLeService getService()
        {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new LocalBinder();


}
