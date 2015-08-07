package kr.ac.sch.cglab.plantmonitor.BLE;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Administrator on 2015-08-03.
 */
public class BluetoothLeService extends Service {
    private static final String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager    mBluetoothManager;
    private BluetoothAdapter    mBluetoothAdapter;

    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED            = "kr.ac.sch.cglab.plantmonitor.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED         = "kr.ac.sch.cglab.plantmonitor.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICE_DISCOVERED   = "kr.ac.sch.cglab.plantmonitor.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_GATT_NOTIFICATION         = "kr.ac.sch.cglab.plantmonitor.ACTION_GATT_NOTIFICATION";
    public final static String ACTION_DATA_AVAILABLE            = "kr.ac.sch.cglab.plantmonitor.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA                       = "kr.ac.sch.cglab.plantmonitor.EXTRA_DATA";

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
            String intentAction;
            if(newState == BluetoothProfile.STATE_CONNECTED)
            {
                intentAction = ACTION_GATT_CONNECTED;   //"kr.ac.sch.cglab.plantmonitor.ACTION_GATT_CONNECTED";
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }
    };


    private void broadcastUpdate(String intentAction) {
        final Intent intent = new Intent(intentAction);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(String intentAction, BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(intentAction);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
