package com.blogspot.jimzhou001.experimentmanager.Adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ColumnAdapter extends PagerAdapter {

    private ArrayList<View> viewList;

    public ColumnAdapter(ArrayList<View> viewList) {
        this.viewList = viewList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = viewList.get(position);
        ((ViewPager)container).addView(view);
        return viewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager)container).removeView(viewList.get(position));
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
