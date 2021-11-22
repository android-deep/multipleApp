package com.ft.mapp.delegate;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.ft.mapp.R;
import com.ft.mapp.VApp;
import com.ft.mapp.home.HomeActivity;
import com.ft.mapp.hook.BackHomeHook;
import com.ft.mapp.hook.MapHook;
import com.ft.mapp.hook.MetaCrmHook;
import com.ft.mapp.hook.StepNumberHook;
import com.ft.mapp.hook.TikTokWaterMark;
import com.ft.mapp.utils.AppSharePref;
import com.ft.mapp.utils.ToastUtil;
import com.fun.vbox.client.VClient;
import com.fun.vbox.client.hook.delegate.ComponentDelegate;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

public class MyComponentDelegate implements ComponentDelegate {

    @Override
    public void beforeStartApplication(String packageName, String processName, Context context) {

    }

    @Override
    public void beforeApplicationCreate(String packageName, String processName,
                                        Application application) {

    }

    @Override
    public void afterApplicationCreate(String packageName, String processName,
                                       Application application) {
        //AirBrushHook.handleLoadPackage();
        TikTokWaterMark.handleLoadPackage();
        StepNumberHook.handleLoadPackage();
        MapHook.handleLoadPackage();
        MetaCrmHook.handleLoadPackage();
        BackHomeHook.handleLoadPackage();
//        ToastUtil.show(application,"packageName="+packageName+",processName="+processName);
        Log.i("--------------","packageName="+packageName+",processName="+processName);
//        showFloating(application);

    }

    private void showFloating(Activity activity) {

//        Application application = activity.getApplication();
//        TextView textView = new TextView(activity);
//        textView.setGravity(Gravity.CENTER);
//        textView.setBackgroundColor(Color.BLACK);
//        textView.setText("111111");
//        textView.setTextSize(10);
//        textView.setTextColor(Color.RED);
//
//        //类型是TYPE_TOAST，像一个普通的Android Toast一样。这样就不需要申请悬浮窗权限了。
//        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_TOAST);
//
//        //初始化后不首先获得窗口焦点。不妨碍设备上其他部件的点击、触摸事件。
//        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        params.width = WindowManager.LayoutParams.MATCH_PARENT;
//        params.height = 300;
//        //params.gravity=Gravity.BOTTOM;
//
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Toast.makeText(getApplication(), "不需要权限的悬浮窗实现", Toast.LENGTH_LONG).show();
//                activity.startActivity(new Intent(activity,HomeActivity.class));
//            }
//        });
//
//        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
//        windowManager.addView(textView, params);

//        ImageView imageView = new ImageView(context);
//        imageView.setImageResource(R.drawable.icon_logo);
//        try{
//            Log.i("--------------","application name="+context.getApplicationInfo().className);
//            int screenWidth = getScreenWidth(context);
//            FloatWindow.B b = FloatWindow
//                    .with(context)
//                    .setView(imageView)
//                    .setWidth(Screen.width, 0.2f) //设置悬浮控件宽高
//                    .setHeight(Screen.width, 0.2f)
//                    .setX(Screen.width, 0.8f)
//                    .setY(Screen.height, 0.3f)
//                    .setMoveType(MoveType.slide, 0, 0)
//                    .setMoveStyle(500, new BounceInterpolator())
//                    .setDesktopShow(true);
//
////                        .setFilter(true, HomeActivity.class)
////                        .setViewStateListener(mViewStateListener)
////                        .setPermissionListener(mPermissionListener)
//
//            b.build();
//            Log.i("--------------","Screen.width="+screenWidth);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                context.startActivity(new Intent(context, HomeActivity.class));
//            }
//        });
//        }catch (Exception e){
//            e.printStackTrace();
////            Log.i("--------------","packageName="+packageName+",processName="+processName);
//        }
    }

    @Override
    public void beforeActivityCreate(Activity activity) {
        String pkgName = VClient.get().getCurrentPackage();
        if ("com.mahjong.sichuang".equals(pkgName)) {
            int height = getScreenHeight(VApp.getApp());
            int width = getScreenWidth(VApp.getApp());

            android.view.WindowManager.LayoutParams p = activity.getWindow().getAttributes();
            p.width = height;
            p.height = width;
            p.gravity = Gravity.TOP | Gravity.LEFT;
            activity.getWindow().setAttributes(p);
        }

    }

    @Override
    public void beforeActivityResume(Activity activity) {

    }

    @Override
    public void beforeActivityPause(Activity activity) {

    }

    @Override
    public void beforeActivityDestroy(Activity activity) {

    }

    @Override
    public void afterActivityCreate(Activity activity) {
        showFloating(activity);
        String pkgName = VClient.get().getCurrentPackage();
        Log.i("--------------","activity="+activity.getLocalClassName());

        if (!"com.wuba.crm".equals(pkgName)) {
            return;
        }
        String activityName = activity.getClass().getName();
        if (activityName.contains("StoreDetailActivity")) {
            try {
                Field f_mPresenter = activity.getClass().getDeclaredField("mPresenter");
                f_mPresenter.setAccessible(true);
                Object mPresenter = f_mPresenter.get(activity);
                Field f_mView = mPresenter.getClass().getDeclaredField("mView");
                f_mView.setAccessible(true);
                Object mView = f_mView.get(mPresenter);

                Class clsView =
                        Class.forName("com.wuba.mobile.plugin.realestatecrm.logic.customer" +
                                        ".storedetail.StoreDetailContract$View", false,
                                VClient.get().getClassLoader());
                Object proxy = Proxy.newProxyInstance(
                        VClient.get().getClassLoader(),
                        new Class[]{clsView},
                        (proxy1, method, args) -> {
                            if (method.getName().equals("openCamera")) {
                                try {
                                    String param = (String) args[0];
                                    if (TextUtils.isEmpty(param)) {
                                        String address =
                                                AppSharePref.getInstance(VApp.getApp()).getString(
                                                        "my_address");
                                        if (TextUtils.isEmpty(address)) {
                                            args[0] = "中国海南省海口市秀英区秀英大道33号";
                                        } else {
                                            args[0] = address;
                                        }
                                        Log.e("WUBA", "fix location error");
                                    } else {
                                        Log.e("WUBA", "location：" + param);
                                    }
                                } catch (Throwable e) {
                                    Log.e("WUBA", "location error", e);
                                }
                            }
                            try {
                                return method.invoke(mView, args);
                            } catch (Throwable th) {
                                return null;
                            }
                        });
                f_mView.set(mPresenter, proxy);
            } catch (Throwable e) {
                Log.e("WUBA", "", e);
            }
        }
    }

    @Override
    public void afterActivityResume(Activity activity) {
    }

    @Override
    public void afterActivityPause(Activity activity) {

    }

    @Override
    public void afterActivityDestroy(Activity activity) {

    }

    @Override
    public Bundle invokeFromAnyWhere(Bundle bundle) {
        return null;
    }

    public static int getScreenWidth(Context context) {
        return getScreenSize(context, null).x;
    }

    public static int getScreenHeight(Context context) {
        return getScreenSize(context, null).y;
    }

    public static Point getScreenSize(Context context, Point outSize) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Point ret = outSize == null ? new Point() : outSize;
        final Display defaultDisplay = wm.getDefaultDisplay();
        defaultDisplay.getSize(ret);
        return ret;
    }
}
