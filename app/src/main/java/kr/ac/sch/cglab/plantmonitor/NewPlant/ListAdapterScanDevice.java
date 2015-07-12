package kr.ac.sch.cglab.plantmonitor.NewPlant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.sch.cglab.plantmonitor.R;

/**
 * Created by Administrator on 2015-07-11.
 */
public class ListAdapterScanDevice extends BaseAdapter
{
    private LayoutInflater mInflater;
    private ArrayList<ScannedBluetoothDevice> mDeviceList;
    private int mLayout;

    public ListAdapterScanDevice(Context context, int layout, ArrayList<ScannedBluetoothDevice> deviceList)
    {
        super();
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mLayout = layout;
        this.mDeviceList = deviceList;
    }

    //리스트에 이미 등록된 디바이가 있는지 검사 후 없으면 새로 추가
    public void addDevice(ScannedBluetoothDevice device){
        int count = 0;
        String ppAddr = device.mDevice.getAddress();

        for (int i = 0; i < mDeviceList.size(); ++i){
            ScannedBluetoothDevice dev = mDeviceList.get(i);
            String newAddr = dev.mDevice.getAddress();

            if(ppAddr.equals(newAddr)) {
                updateDeviceRSSI(dev, device.mRSSI);        //기존에 있는 디바이스면 RSSI 정보를 업데이트
                count++;
            }
        }

        if(count == 0)
            mDeviceList.add(device);
    }

    //리스트에 이미 등록된 디바이스 검사후 RSSI 업데이터
    public void updateDeviceRSSI(ScannedBluetoothDevice device, int rssi)
    {
        device.mRSSI = rssi;
    }
    public void updateDeviceRSSI(ScannedBluetoothDevice device)
    {
        int count = 0;
        String ppAddr = device.mDevice.getAddress();

        for (int i = 0; i < mDeviceList.size(); ++i){
            String addr = mDeviceList.get(i).mDevice.getAddress();

            if(ppAddr.equals(addr)) {
                mDeviceList.get(i).mRSSI = device.mRSSI;    //rssi 를 새로 갱신
            }
        }
    }

    @Override
    public int getCount() {
        return this.mDeviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mDeviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = mInflater.inflate(mLayout, parent, false);

        ScannedBluetoothDevice device = mDeviceList.get(position);

        //디바이스 이름 표시 / mac 주소 표시
        ((TextView)convertView.findViewById(R.id.list_Adapter_new_device_txtBx_device_name)).setText(device.mDevice.getName());
        ((TextView)convertView.findViewById(R.id.list_Adapter_new_device_txtBx_device_address)).setText(device.mDevice.getAddress());

        String rssiStr = "";
        if(device.mRSSI <= -60){
            rssiStr = "가까이 있음";
        }
        else if(-60 < device.mRSSI && device.mRSSI <= -85 ){
            rssiStr = "근처에 있음";
        }
        else
            rssiStr = "멀리 떨어짐";

        //rssi 분석후 거리 표시
        ((TextView)convertView.findViewById(R.id.list_Adapter_new_device_txtBx_device_rssi)).setText(rssiStr);

        return convertView;
    }
}
