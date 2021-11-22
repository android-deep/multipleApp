package com.ft.mapp.utils;

import android.content.Context;

import com.ft.mapp.VApp;

public class SizeUtils {

    public static int dip2px(float dipValue) {
        final float scale = VApp.getApp().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
