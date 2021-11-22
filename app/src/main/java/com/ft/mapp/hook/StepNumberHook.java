package com.ft.mapp.hook;

import android.annotation.SuppressLint;
import android.util.Log;

import com.ft.mapp.utils.AppSharePref;
import com.fun.vbox.client.VClient;
import com.fun.vbox.client.core.VCore;

import andhook.lib.xposed.XC_MethodHook;
import andhook.lib.xposed.XposedBridge;

public class StepNumberHook {
    private static final String TAG = "StepNumberHook";
    private static int stepCount = 1;

    public static void handleLoadPackage() {
        String pkgName = VClient.get().getCurrentPackage();
        int stepTimes = AppSharePref.getInstance(VCore.get().getContext()).getInt(pkgName +
                "_stepTimes", 1);

        try {
            @SuppressLint("PrivateApi")
            final Class<?> sensorEL = Class.forName("android.hardware.SystemSensorManager$SensorEventQueue",
                    true, VClient.get().getClassLoader());

            XposedBridge.hookAllMethods(
                    sensorEL,
                    "dispatchSensorEvent",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            ((float[]) param.args[1])[0] = ((float[]) param.args[1])[0] + stepTimes * stepCount;
                            stepCount++;
                        }
                    });
        } catch (Throwable e) {
            Log.e(TAG, "error", e);
        }
    }
}
