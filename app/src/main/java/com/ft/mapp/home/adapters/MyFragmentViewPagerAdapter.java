package com.ft.mapp.home.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MyFragmentViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> list;

    public MyFragmentViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
        super(fm);
        this.list = list;

    }

    @Override//返回要显示的碎片
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override//返回要显示多少页
    public int getCount() {
        return list.size();
    }

}
