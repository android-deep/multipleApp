package com.ft.mapp.home.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class LaunchAdapter extends PagerAdapter {

    public LaunchAdapter() {
    }

    private List<RecyclerView> mLauncherViews;

    public void setList(List<RecyclerView> mLauncherViews){
        this.mLauncherViews = mLauncherViews;
    }

    @Override
    public int getCount() {
        return mLauncherViews.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(mLauncherViews.get(position));
        return mLauncherViews.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
