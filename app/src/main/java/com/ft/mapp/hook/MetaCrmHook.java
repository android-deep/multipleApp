package com.ft.mapp.hook;

import android.text.TextUtils;
import android.util.Log;

import com.ft.mapp.VApp;
import com.ft.mapp.utils.AppSharePref;
import com.fun.vbox.client.VClient;

import andhook.lib.xposed.XC_MethodHook;
import andhook.lib.xposed.XposedHelpers;

public class MetaCrmHook {
    private static final String TAG = "metaCrm";

    public static void handleLoadPackage() {
        String pkgName = VClient.get().getCurrentPackage();
        if (!"com.metasoft.metacrm.app6".equals(pkgName)) {
            return;
        }

        //百度
        try {
            XposedHelpers.findAndHookMethod(
                    "com.baidu.location.BDLocation",
                    VClient.get().getClassLoader(),
                    "getAddrStr",
                    new XC_MethodHook() {

                        @Override
                        protected void afterHookedMethod(MethodHookParam param)
                                throws Throwable {
                            super.afterHookedMethod(param);
                            String object = (String) param.getResult();
                            if (TextUtils.isEmpty(object)) {
                                String address =
                                        AppSharePref.getInstance(VApp.getApp()).getString(
                                                "my_address");
                                param.setResult(address);
                                Log.e(TAG, "new:" + address);
                            } else {
                                Log.e(TAG, "old:" + object);
                            }
                        }
                    });
        } catch (Throwable e) {
            //Log.e(TAG, "error", e);
        }
    }
}
