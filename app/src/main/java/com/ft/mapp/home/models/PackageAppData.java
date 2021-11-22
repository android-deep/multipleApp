package com.ft.mapp.home.models;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.fun.vbox.remote.InstalledAppInfo;

/**
 *
 */
public class PackageAppData implements AppData {

    public String packageName;
    public String name;
    public Drawable icon;
    public boolean fastOpen;
    public boolean isFirstOpen;
    public boolean isLoading;
    public int appId;
    public FakeAppInfo fakeAppInfo;

    public PackageAppData(Context context, InstalledAppInfo installedAppInfo) {
        this.packageName = installedAppInfo.packageName;
        this.isFirstOpen = !installedAppInfo.isLaunched(0);
        this.appId = installedAppInfo.appId;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, 0);
            loadData(context, info);
        } catch (PackageManager.NameNotFoundException ignore) {

        }
    }

    private void loadData(Context context, ApplicationInfo appInfo) {
        if (appInfo == null) {
            return;
        }
        PackageManager pm = context.getPackageManager();
        try {
            CharSequence sequence = appInfo.loadLabel(pm);
            name = sequence.toString();
            icon = appInfo.loadIcon(pm);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void setFakeIcon(String fakeIcon){

    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public boolean isFirstOpen() {
        return isFirstOpen;
    }

    @Override
    public Drawable getIcon() {
        return icon;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public int getAppId() {
        return appId;
    }

    @Override
    public int getUserId() {
        return 0;
    }

    @Override
    public FakeAppInfo getFakeAppInfo() {
        return fakeAppInfo;
    }

    @Override
    public boolean canReorder() {
        return true;
    }

    @Override
    public boolean canLaunch() {
        return true;
    }

    @Override
    public boolean canDelete() {
        return true;
    }

    @Override
    public boolean canCreateShortcut() {
        return true;
    }
}
