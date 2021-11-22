package com.ft.mapp.hook;

import android.util.Log;

import com.fun.vbox.client.VClient;

import andhook.lib.xposed.XC_MethodHook;
import andhook.lib.xposed.XposedHelpers;

public class MapHook {
    private static final String TAG = "map";

    public static void handleLoadPackage() {
        //百度
        try {
            Class clsParam = VClient.get().getClassLoader().loadClass("com.baidu.location" +
                    ".LocationClientOption$LocationMode");
            XposedHelpers.findAndHookMethod(
                    "com.baidu.location.LocationClientOption",
                    VClient.get().getClassLoader(),
                    "setLocationMode",
                    clsParam,
                    new XC_MethodHook() {

                        @Override
                        protected void beforeHookedMethod(MethodHookParam param)
                                throws Throwable {
                            super.beforeHookedMethod(param);
                            param.args[0] = clsParam.getEnumConstants()[2];
                            Log.e(TAG, "setLocationMode:" + param.args[0]);
                        }
                    });
        } catch (Throwable e) {
            //Log.e(TAG, "error", e);
        }

        //高德
        try {
            Class clsParam = VClient.get().getClassLoader().loadClass("com.amap.api.location" +
                    ".AMapLocationClientOption$AMapLocationMode");
            XposedHelpers.findAndHookMethod(
                    "com.amap.api.location.AMapLocationClientOption",
                    VClient.get().getClassLoader(),
                    "setLocationMode",
                    clsParam,
                    new XC_MethodHook() {

                        @Override
                        protected void beforeHookedMethod(MethodHookParam param)
                                throws Throwable {
                            super.beforeHookedMethod(param);
                            param.args[0] = clsParam.getEnumConstants()[1];
                            Log.e(TAG, "setLocationMode:" + param.args[0]);
                        }
                    });
        } catch (Throwable e) {
            //Log.e(TAG, "error", e);
        }
    }
}
