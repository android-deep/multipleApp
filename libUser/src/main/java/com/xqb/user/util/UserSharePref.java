package com.xqb.user.util;

import android.content.Context;
import android.content.SharedPreferences;


public class UserSharePref {

    private SharedPreferences mSharedPreferences = null;
    private static volatile UserSharePref sSnapSharePref;

    public static UserSharePref getInstance(Context context) {
        if (sSnapSharePref == null) {
            synchronized (UserSharePref.class) {
                if (sSnapSharePref == null) {
                    sSnapSharePref = new UserSharePref(context);
                }
            }
        }
        return sSnapSharePref;
    }

    private UserSharePref(Context context) {
        mSharedPreferences = context.getApplicationContext().getSharedPreferences("user_sp.ser",
                Context.MODE_MULTI_PROCESS);
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
    public static final String KEY_USER_INFO = "user_info";
    public static final String KEY_PRODUCT_INFO = "product_info";
    public static final String KEY_PENDING_USER_INFO = "pending_user_info";
    public static final String KEY_LOGINED = "key_logined";
    public static final String KEY_VERION_INFO = "verion_info";
    public static final String KEY_HAS_RECEIVE_GIFT = "has_receive_gift";
    public static final String KEY_FIRST_INSTALL = "first_install";
    public static final String KEY_FIRST_USE = "first_use";
    public static final String KEY_GUIDE_ADD_APP = "guide_add_app";
    public static final String KEY_GUIDE_ADD_APP_FROM_LIST = "guide_add_app_from_list";
    public static final String KEY_GUIDE_FUNCTION = "guide_function";
    public static final String KEY_GUIDE_VIP = "guide_vip";
    public static final String KEY_ORDER_STATUS = "order_status";
    public static final String KEY_ANDROID_ID = "android_id";


}
