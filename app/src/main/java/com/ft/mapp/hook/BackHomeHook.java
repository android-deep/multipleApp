package com.ft.mapp.hook;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.ft.mapp.R;
import com.ft.mapp.delegate.IPlugin;
import com.ft.mapp.delegate.MultiPlugin;
import com.ft.mapp.home.HomeActivity;
import com.ft.mapp.listener.SimpleDownloadListener;
import com.ft.mapp.utils.CommonUtil;
import com.fun.vbox.client.VClient;
import com.fun.vbox.client.core.VCore;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Objects;

import andhook.lib.xposed.XC_MethodHook;
import andhook.lib.xposed.XposedHelpers;

public class BackHomeHook {
    private static final String TAG = "BackHomeHook";
    private static HashMap<String, MultiPlugin> sMapFloatView = new HashMap<>();
    private static WeakReference<MultiPlugin> sCurFloatView;
    public static void handleLoadPackage() {
        Log.e(TAG, "handleLoadPackage:");
        try {
            XposedHelpers.findAndHookMethod(Activity.class, "onResume",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            Log.e(TAG, "afterHookedMethod:");
                            Activity act = (Activity) param.thisObject;
                            String key = act.getClass().hashCode() + "";
                            Log.e(TAG, "onResume:" + act.getClass().getCanonicalName() + " key:" +
                            key);
                            MultiPlugin floatPlugin = sMapFloatView.get(key);
                            if (floatPlugin == null) {
                                floatPlugin = new MultiPlugin();
                                sMapFloatView.put(key, floatPlugin);
                                floatPlugin.showNow(act);
                            }
                            sCurFloatView = new WeakReference<>(floatPlugin);
                        }
                    });

            XposedHelpers.findAndHookMethod(Activity.class, "onDestroy",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            Activity act = (Activity) param.thisObject;
                            String key = act.getClass().hashCode() + "";
                            Log.e(TAG, "onDestroy:" + act.getClass().getCanonicalName() + " key:" +
                                    key);
                            MultiPlugin floatView = sMapFloatView.get(key);
                            if (floatView != null) {
                                floatView.destroy(act);
                                sMapFloatView.remove(key);
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "error", e);
        }
    }

}
