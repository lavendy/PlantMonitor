package kr.ac.sch.cglab.plantmonitor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

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
            //plantImgIcon.setImageBitmap(FileManager.getImgFromFile(PlantsDataManager.getPlantImgNum(position)));

            //식물 이름  표시
            TextView txtName = (TextView) v.findViewById(R.id.list_adp_monitor_text_plant_name);
            txtName.setText( PlantsDataManager.getPlantName(position) );

            //식물 상태 표시
            TextView txtStatus = (TextView) v.findViewById(R.id.list_adp_monitor_text_plant_status);
            txtStatus.setText(PlantsDataManager.getPlantStatus(position));
            txtStatus.setSelected(true);
            txtStatus.setEllipsize(TextUtils.TruncateAt.MARQUEE);   //전광판 효과 흘러가는 효과
            txtStatus.setMarqueeRepeatLimit(-1);    // repeats indefinitely


            //온도 습도 조도 표시
            TextView txtHumidity = (TextView) v.findViewById(R.id.list_adp_monitor_text_humidity);
            txtHumidity.setText(PlantsDataManager.getPlantHumidityStr(position));

            TextView txtTemperature = (TextView) v.findViewById(R.id.list_adp_monitor_text_temperature);
            txtTemperature.setText(PlantsDataManager.getPlantTemperatureStr(position));

            TextView txtLux = (TextView) v.findViewById(R.id.list_adp_monitor_text_lux);
            txtLux.setText(PlantsDataManager.getPlantLuxStr(position));

            //프로그래스바 표시
            ProgressBar pbHumidity = (ProgressBar) v.findViewById(R.id.list_adp_monitor_progress_humidity);
            pbHumidity.setProgress(PlantsDataManager.getPlantHumidity(position));
            setProgressBarColor(pbHumidity, PlantsDataManager.INFO_TYPE.HUMIDITY, position);    //상태 얻어서 프로그레스바 색표시

            ProgressBar pbTemperature = (ProgressBar) v.findViewById(R.id.list_adp_monitor_progress_temperature);
            pbTemperature.setProgress( PlantsDataManager.getPlantTemperature(position) );
            setProgressBarColor(pbTemperature, PlantsDataManager.INFO_TYPE.TEMPERATURE, position);

            ProgressBar pbLux= (ProgressBar) v.findViewById(R.id.list_adp_monitor_progress_lux);
            pbLux.setProgress( PlantsDataManager.getPlantLux(position));
            setProgressBarColor(pbLux, PlantsDataManager.INFO_TYPE.LUX, position);

        }
        return v;
    }

    private void setProgressBarColor(ProgressBar pb, PlantsDataManager.INFO_TYPE type, int position)
    {
        if(type == PlantsDataManager.INFO_TYPE.HUMIDITY)
            SetPBColor(pb, PlantsDataManager.checkHumidity(position));
        else if(type == PlantsDataManager.INFO_TYPE.TEMPERATURE)
            SetPBColor(pb, PlantsDataManager.checkTemperature(position));
        else if(type == PlantsDataManager.INFO_TYPE.LUX)
            SetPBColor(pb, PlantsDataManager.checkLux(position));
    }
    private void SetPBColor(ProgressBar bar, PlantsDataManager.PROPERTIES_STATUS status)
    {
        //low 파랑 //normal 초록 //high 빨강
        int color = Color.BLACK;
        if(status == PlantsDataManager.PROPERTIES_STATUS.LOW)
            color = Color.BLUE;
        else if(status == PlantsDataManager.PROPERTIES_STATUS.NORMAL)
            color = Color.GREEN;
        else if(status == PlantsDataManager.PROPERTIES_STATUS.HIGH)
            color = Color.RED;

        bar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }
}
