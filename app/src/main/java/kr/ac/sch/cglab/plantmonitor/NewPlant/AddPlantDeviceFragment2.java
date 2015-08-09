package kr.ac.sch.cglab.plantmonitor.NewPlant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import kr.ac.sch.cglab.plantmonitor.R;

/**
 * Created by Administrator on 2015-08-09.
 */
public class AddPlantDeviceFragment2 extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.activity_add_plant_p2, container, false);

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
}
