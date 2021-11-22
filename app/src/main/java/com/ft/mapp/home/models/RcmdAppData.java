package com.ft.mapp.home.models;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.ft.mapp.VApp;

public class RcmdAppData implements AppData {
    public String packageName;
    public String name;
    public Drawable icon;

    public RcmdAppData(String packageName) {
        this.packageName = packageName;
        try {
            ApplicationInfo info = VApp.getApp().getPackageManager().getApplicationInfo(getPackageName(), 0);
            loadData(VApp.getApp(), info);
        } catch (PackageManager.NameNotFoundException e) {

        }
    }

    private void loadData(Context context, ApplicationInfo appInfo) {
        if (appInfo == null) {
            return;
        }
        PackageManager pm = context.getPackageManager();
        try {
            CharSequence sequence = appInfo.loadLabel(pm);
            if (sequence != null) {
                name = sequence.toString();
            }
            icon = appInfo.loadIcon(pm);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isLoading() {
        return false;
    }

    @Override
    public boolean isFirstOpen() {
        return true;
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
        return 0;
    }

    @Override
    public int getUserId() {
        return 0;
    }

    @Override
    public FakeAppInfo getFakeAppInfo() {
        return null;
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
        return false;
    }
}
