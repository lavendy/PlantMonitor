package kr.ac.sch.cglab.plantmonitor.NewPlant;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015-07-10.
 */
public class ViewPagerAdapterAddPlant extends PagerAdapter implements ViewPager.OnPageChangeListener
{
    private ArrayList<View> mPageViews;
    private Context mContext;

    public ViewPagerAdapterAddPlant(ArrayList<View> pageViews, Context context)
    {
        this.mPageViews = pageViews;
        this.mContext = context;
    }

    @Override
    public Object instantiateItem(View container, int position) {
        switch (position)
        {
            case 0: //0번 페이지 초기화
                break;
            case 1: //1번 페이지 초기화
                break;
        }
        ((ViewPager) container).addView(this.mPageViews.get(position));
        return this.mPageViews.get(position);
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager)container).removeView(this.mPageViews.get(position));
    }

    @Override
    public int getCount() {
        return this.mPageViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }


    //OnPageChangeListener
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
