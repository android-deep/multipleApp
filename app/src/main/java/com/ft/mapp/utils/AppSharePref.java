package com.ft.mapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ft.mapp.VApp;


public class AppSharePref {

    private SharedPreferences mSharedPreferences = null;
    private static volatile AppSharePref sSnapSharePref;

    public static AppSharePref getInstance(Context context) {
        if (sSnapSharePref == null) {
            synchronized (AppSharePref.class) {
                if (sSnapSharePref == null) {
                    sSnapSharePref = new AppSharePref(context);
                }
            }
        }
        return sSnapSharePref;
    }

    private AppSharePref(Context context) {
        if (context == null) {
            context = VApp.getApp();
        }
        mSharedPreferences = context.getSharedPreferences("v_app", Context.MODE_MULTI_PROCESS);
    }

    /**************************基本方法定义************************************/
    public void putBoolean(String keyString, boolean value) {
        mSharedPreferences.edit().putBoolean(keyString, value).apply();
    }

    public boolean getBoolean(String keyString) {
        return mSharedPreferences.getBoolean(keyString, false);
    }

    public boolean getBoolean(String keyString, boolean defValue) {
        return mSharedPreferences.getBoolean(keyString, defValue);
    }

    public void putString(String keyString, String value) {
        mSharedPreferences.edit().putString(keyString, value).apply();
    }

    public String getString(String keyString) {
        return mSharedPreferences.getString(keyString, "");
    }

    public void putInt(String keyString, int value) {
        mSharedPreferences.edit().putInt(keyString, value).apply();
    }

    public int getInt(String keyString) {
        return mSharedPreferences.getInt(keyString, -1);
    }

    public int getInt(String keyString, int defaultValue) {
        return mSharedPreferences.getInt(keyString, defaultValue);
    }

    public void putLong(String keyString, long value) {
        mSharedPreferences.edit().putLong(keyString, value).apply();
    }

    public long getLong(String keyString) {
        return mSharedPreferences.getLong(keyString, -1);
    }

    public long getLong(String keyString, long defValue) {
        return mSharedPreferences.getLong(keyString, defValue);
    }

    /***************************业务 KEY 的定义***********************************/
    public static final String KEY_IS_FIRST = "is_first";
    public static final String KEY_TIKTOK_ANIMATOR_SHOW = "tiktok_animator_show"; //只展示一次，记录是否展示过
    public static final String KEY_TIK_PLUGIN_ENABLE = "tik_plugin_enable"; //是否开启了无水印

    public static final String KEY_RATE_CLICK = "rate_click"; //评了多少星
    public static final String KEY_LAST_LAUNCH_TIME = "last_launch_time"; //7天内上次启动时间，用来计算是否高频用户
    public static final String KEY_WEEK_LAUNCH_TIME = "week_launch_time"; //7天内上次启动时间，用来计算是否高频用户
    public static final String KEY_WEEK_LAUNCH_NUM = "week_launch_num"; //7天内启动了几次
    public static final String KEY_RATE_SHOW_TIME = "rate_show_time"; //评星卡片上次显示时间
    public static final String KEY_AGREE_POLICY = "agree_policy";


}
