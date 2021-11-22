package com.ft.mapp.home.models;

import android.graphics.drawable.Drawable;

public class AddAppData implements AppData {

    @Override
    public boolean isLoading() {
        return false;
    }

    @Override
    public boolean isFirstOpen() {
        return false;
    }

    @Override
    public Drawable getIcon() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getPackageName() {
        return null;
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
        return false;
    }

    @Override
    public boolean canLaunch() {
        return false;
    }

    @Override
    public boolean canDelete() {
        return false;
    }

    @Override
    public boolean canCreateShortcut() {
        return false;
    }
}
