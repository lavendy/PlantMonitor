package kr.ac.sch.cglab.plantmonitor;

import android.content.ClipData;
import android.content.Context;
import android.service.carrier.MessagePdu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import javax.net.ssl.ManagerFactoryParameters;

import kr.ac.sch.cglab.plantmonitor.Data.FileManager;
import kr.ac.sch.cglab.plantmonitor.Data.PlantData;
import kr.ac.sch.cglab.plantmonitor.Data.PlantsDataManager;

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
            LayoutInflater vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_adapter_plant_monitor, null);
        }

        PlantData pData = getItem(position);

        if(pData != null)
        {
            //파일에서 이미지 로드 아이콘 지정
            ImageView plantImgIcon = (ImageView) v.findViewById(R.id.list_adp_monitor_img_view_plant);
            plantImgIcon.setImageBitmap(FileManager.getImgFromFile(PlantsDataManager.getPlantImgNum(position)));

            //식물 정보 표시
            TextView txtName = (TextView) v.findViewById(R.id.list_adp_monitor_text_plant_name);
            txtName.setText( PlantsDataManager.getPlantName(position) );
            
            TextView txtStatus = (TextView) v.findViewById(R.id.list_adp_monitor_text_plant_status);
            txtStatus.setText(PlantsDataManager.getPlantStatus(position));

            //온도 습도 조도 표시
            TextView txtHumidity = (TextView) v.findViewById(R.id.list_adp_monitor_text_humidity);
            txtHumidity.setText( PlantsDataManager.getPlantHumidityInfo(position));

            TextView txtTemperature = (TextView) v.findViewById(R.id.list_adp_monitor_text_temperature);
            txtTemperature.setText( PlantsDataManager.getPlantTemperatrueInfo(position));

            TextView txtLux = (TextView) v.findViewById(R.id.list_adp_monitor_text_lux);
            txtLux.setText(PlantsDataManager.getPlantLuxInfo(position));

            //프로그래스바 표시
        }
        return v;
    }
}
