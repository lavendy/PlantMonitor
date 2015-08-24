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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
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
import java.util.List;
import java.util.UUID;

import kr.ac.sch.cglab.plantmonitor.BLE.DeviceData;
import kr.ac.sch.cglab.plantmonitor.BLE.GattAttributes;
import kr.ac.sch.cglab.plantmonitor.Data.PlantData;
import kr.ac.sch.cglab.plantmonitor.R;



public class AddPlantActivity extends FragmentActivity implements View.OnTouchListener  {

    private static final String TAG = AddPlantActivity.class.getSimpleName();

    public final static int FRAGMENT_P0 = 0;
    public final static int FRAGMENT_P1 = 1;
    public final static int FRAGMENT_P2 = 2;

    //fragment control view gui
    private int mCurrentFragmentIndex;

    private Button mBtnExit;
    private Button mBtnBack;
    private Button mBtnCommit;

    private boolean mSelectPlant = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant_viewpager);

        //버튼
        this.mBtnExit = (Button) findViewById(R.id.activity_add_plant_viewpager_btn_exit);

        this.mBtnExit.setOnTouchListener(this);

        mCurrentFragmentIndex = FRAGMENT_P0;
        fragmentReplace(mCurrentFragmentIndex);
    }


    //controls fragment from other fragment
    public void moveToFragment1(BluetoothDevice device)    //called from fragment p0
    {
        AddPlantDeviceFragment1 p1Fragment = (AddPlantDeviceFragment1) fragmentReplace(FRAGMENT_P1);

        p1Fragment.connectBLE(device);  //1페이지 fragment 는 gatt server에 연결
    }
    public void moveToFragment2(PlantData plantData)
    {
        AddPlantDeviceFragment2 p2Fragment = (AddPlantDeviceFragment2)fragmentReplace(FRAGMENT_P2);
        p2Fragment.readyToRegisterPlant(plantData);
    }
    public void commitPlantToDB()
    {

    }


    private Fragment fragmentReplace(int currentFragmentIndex)
    {
        mCurrentFragmentIndex = currentFragmentIndex;   //새로운 fragment 를 현재로 세팅

        Fragment newFragment = getFragment(currentFragmentIndex);

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_add_plant_view_fragment_control_view, newFragment, ""+currentFragmentIndex);
        transaction.commit();       //fragment 변경!

        updateButtonGUI();          //버튼 gui변경

        return newFragment;
    }

    private void updateButtonGUI()
    {
        if(mCurrentFragmentIndex == FRAGMENT_P0)
        {
            mSelectPlant = false;   //처음 페이지에서는 식물이 선택 되지 않음
        }
        else if(mCurrentFragmentIndex == FRAGMENT_P1)
        {
        }
        else if(mCurrentFragmentIndex == FRAGMENT_P2);
        {
        }
    }

    private Fragment getFragment(int index)
    {
        switch (index)
        {
            case FRAGMENT_P0:
                return new AddPlantDeviceFragment0();
            case FRAGMENT_P1:
                return new AddPlantDeviceFragment1();
            case FRAGMENT_P2:
                return new AddPlantDeviceFragment2();
            default:
                Log.d(TAG,"Could not make AddPlantDeviceFragment");
                break;
        }
        return null;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //우측 상단 누르면 plant 등록 액티비티 종료
        if( event.getAction() == MotionEvent.ACTION_UP){
            if(v.getId() == R.id.activity_add_plant_viewpager_btn_exit)     //우측 상단 x 버튼 누르면 종료
            {
                finish();
                return true;
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

    public void moveToPreviousPage(){
        if(mCurrentFragmentIndex == FRAGMENT_P0){    //맨처음 페이지에서 뒤로 누르면 액티비티 종료
            finish();
        }
        else{
            mCurrentFragmentIndex--;
            fragmentReplace(mCurrentFragmentIndex);
        }
    }

    public void successRegistration()
    {
        finish();
    }
}
