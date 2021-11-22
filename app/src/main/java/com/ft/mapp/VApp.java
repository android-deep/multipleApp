package com.ft.mapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import androidx.multidex.MultiDexApplication;

import com.ft.mapp.delegate.MyAppRequestListener;
import com.ft.mapp.delegate.MyComponentDelegate;
import com.ft.mapp.delegate.MyTaskDescriptionDelegate;
import com.ft.mapp.delegate.VirtualEngineDelegate;
import com.ft.mapp.home.BackHomeActivity;
import com.ft.mapp.open.ShortcutHandleActivity;
import com.ft.mapp.utils.AppPackageCompat;
import com.ft.mapp.utils.FakeAppUtils;
import com.fun.vbox.client.core.HostApp;
import com.fun.vbox.client.core.SettingConfig;
import com.fun.vbox.client.core.VCore;
import com.fun.vbox.helper.utils.Reflect;
import com.liulishuo.okdownload.OkDownloadProvider;
import com.qq.e.comm.managers.GDTADManager;
import com.qq.e.comm.managers.setting.GlobalSetting;
import com.xqb.user.util.StatUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import jonathanfinerty.once.Once;
import me.weishu.reflection.Reflection;

//import com.yilan.sdk.ui.YLUIInit;

public class VApp extends MultiDexApplication {

    private static VApp gApp;
    private SharedPreferences mPreferences;

    private SettingConfig mConfig = new SettingConfig() {
        @Override
        public String getHostPackageName() {
            return BuildConfig.APPLICATION_ID;
        }

        @Override
        public String get64bitEnginePackageName() {
            return BuildConfig.PACKAGE_NAME_ARM64;
        }

        @Override
        public String get64bitEngineLaunchActivityName() {
            return "com.zb.vv.EmptyActivity";
        }

        @Override
        public String getShortcutProxyActivityName() {
            return ShortcutHandleActivity.class.getName();
        }

        @Override
        public String getShortcutProxyActionName() {
            return BuildConfig.APPLICATION_ID + ".vbox.action.shortcut";
        }

        @Override
        public boolean isEnableIORedirect() {
            return true;
        }

        @Override
        public Intent onHandleLauncherIntent(Intent originIntent) {
            Intent intent = new Intent(VCore.get().getContext(), BackHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return intent;
        }

        @Override
        public boolean isUseRealDataDir(String packageName) {
            if ("com.eg.android.AlipayGphone".equals(packageName)) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public AppLibConfig getAppLibConfig(String packageName) {
            return AppLibConfig.UseRealLib;
        }

        @Override
        public boolean isAllowCreateShortcut() {
            return false;
        }

        /**
         * 如果返回[true], 则认为将要启动的Activity在宿主之中。
         * @param intent
         * @return
         */
        @Override
        public boolean isHostIntent(Intent intent) {
            return intent.getData() != null && "market".equals(intent.getData().getScheme());
        }
    };

    public static VApp getApp() {
        return gApp;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        mPreferences = base.getSharedPreferences("va", Context.MODE_MULTI_PROCESS);

        if (Build.VERSION.SDK_INT < 30) {
            Reflection.unseal(base);
        }
        HostApp.setApplication(this);
        try {
            VCore.get().startup(base, mConfig);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            closeAndroidPDialog();
        }
    }

    @Override
    public void onCreate() {
        gApp = this;
        super.onCreate();
        init();
        HostApp.setApplication(this);

//        if (VCore.get().getProcessName().equals(android.os.Process.myPid())) {
//        showFloating();
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = VCore.get().getProcessName();
            if (!getPackageName().equals(processName)) {
                int nameIndex = processName.indexOf(":");
                String name = processName.substring(nameIndex + 1);
                WebView.setDataDirectorySuffix(name + "_ft");
            }
        }
        GDTADManager.getInstance().initWith(this, "1111311205");
        GlobalSetting.setChannel(StatUtils.getGDTChannelCode(this));
        bindLifeCycle();
    }

    private void bindLifeCycle() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.i("lifecycletestttttt", activity.getPackageName() + "--" + activity.getLocalClassName());
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    private void init() {
        FakeAppUtils.init(this);
        VCore virtualCore = VCore.get();

        virtualCore.initialize(new VCore.VirtualInitializer() {

            @Override
            public void onMainProcess() {
                Once.initialise(VApp.this);
                AppPackageCompat.putPackageGroup(VConstant.TikTok_PKG, "com.zhiliaoapp.musically,com.ss.android.ugc.aweme");
            }

            @Override
            public void onVirtualProcess() {
                //listener components
                virtualCore.setAppCallback(new MyComponentDelegate());
                //fake task description's icon and title
                virtualCore.setTaskDescriptionDelegate(new MyTaskDescriptionDelegate());
            }

            @Override
            public void onServerProcess() {
                virtualCore.setAppRequestListener(new MyAppRequestListener(VApp.this));
                virtualCore.addVisibleOutsidePackage("com.tencent.mobileqq");
                virtualCore.addVisibleOutsidePackage("com.tencent.mobileqqi");
                virtualCore.addVisibleOutsidePackage("com.tencent.minihd.qq");
                virtualCore.addVisibleOutsidePackage("com.tencent.qqlite");
                //virtualCore.addVisibleOutsidePackage("com.facebook.katana");
                virtualCore.addVisibleOutsidePackage("com.whatsapp");
                virtualCore.addVisibleOutsidePackage("com.tencent.mm");
                virtualCore.addVisibleOutsidePackage("com.immomo.momo");

                virtualCore.setVirtualEngineCallback(new VirtualEngineDelegate());
                Bundle initBundle = virtualCore.getInitBundle();
                if (initBundle != null) {
                    virtualCore.getVirtualEngineCallback().invokeFromAnyWhere(initBundle);
                }
            }
        });

        Reflect.on(OkDownloadProvider.class).set("context", VCore.get().getContext());
    }

    public static SharedPreferences getPreferences() {
        return getApp().mPreferences;
    }

    private void closeAndroidPDialog() {
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
