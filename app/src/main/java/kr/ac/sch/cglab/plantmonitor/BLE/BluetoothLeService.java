package kr.ac.sch.cglab.plantmonitor.BLE;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Administrator on 2015-08-03.
 */
public class BluetoothLeService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
