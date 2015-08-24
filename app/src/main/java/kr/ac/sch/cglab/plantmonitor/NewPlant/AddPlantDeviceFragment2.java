package kr.ac.sch.cglab.plantmonitor.NewPlant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import kr.ac.sch.cglab.plantmonitor.Data.PlantData;
import kr.ac.sch.cglab.plantmonitor.R;

/**
 * Created by Administrator on 2015-08-09.
 */
public class AddPlantDeviceFragment2 extends Fragment implements View.OnTouchListener {
    private Button mBtnConfirm;
    private Button mBtnBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.activity_add_plant_p2, container, false);

        this.mBtnConfirm = (Button) v.findViewById(R.id.activity_add_plant_p2_btn_commit);
        this.mBtnBack = (Button) v.findViewById(R.id.activity_add_plant_p2_btn_back);

        this.mBtnConfirm.setOnTouchListener(this);
        this.mBtnBack.setOnTouchListener(this);

        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    //ready to register plant data
    public void readyToRegisterPlant(PlantData plantData)
    {
        //플랜드 데이터 등록 준비
    }


    //register device to B
    public void commitDevice() {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP)
        {
            if(v.getId() == R.id.activity_add_plant_p2_btn_back)
            {
                ((AddPlantActivity)getActivity()).moveToPreviousPage();
            }
            else if(v.getId() == R.id.activity_add_plant_p2_btn_commit)
            {
                ((AddPlantActivity)getActivity()).successRegistration();
            }
        }
        return false;
    }
}
