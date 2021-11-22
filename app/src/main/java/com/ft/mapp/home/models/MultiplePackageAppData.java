package com.ft.mapp.home.models;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.fun.vbox.client.core.VCore;
import com.fun.vbox.remote.InstalledAppInfo;

/**
 *
 */

public class MultiplePackageAppData implements AppData {

    public InstalledAppInfo appInfo;
    public int userId;
    public boolean isFirstOpen;
    public boolean isLoading;
    public Drawable icon;
    public String name;
    public FakeAppInfo fakeAppInfo;

    public MultiplePackageAppData(PackageAppData target, int userId) {
        this.userId = userId;
        this.appInfo = VCore.get().getInstalledAppInfo(target.packageName, 0);
        this.fakeAppInfo = target.getFakeAppInfo();
        this.isFirstOpen = !appInfo.isLaunched(userId);
        if (target.icon != null) {
            Drawable.ConstantState state = target.icon.getConstantState();
            if (state != null) {
                icon = state.newDrawable();
            }
        }
        name = target.name;
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
        return appInfo.packageName;
    }

    @Override
    public int getAppId() {
        return appInfo.appId;
    }

    @Override
    public int getUserId() {
        return userId;
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
