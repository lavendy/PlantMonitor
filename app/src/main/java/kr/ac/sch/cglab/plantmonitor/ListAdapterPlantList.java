package kr.ac.sch.cglab.plantmonitor;

import android.content.ClipData;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import java.util.List;

import kr.ac.sch.cglab.plantmonitor.Data.PlantData;

/**
 * Created by wind_ on 2015-07-03.
 */
public class ListAdapterPlantList extends ArrayAdapter<PlantData>{

    public ListAdapterPlantList(Context context, int textViewResourceID)
    {
        super(context, textViewResourceID);
    }
    public ListAdapterPlantList(Context context, int resourceID, List<PlantData> items)
    {
        super(context, resourceID, items);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null)
        {

        }
        return null;
    }
}
