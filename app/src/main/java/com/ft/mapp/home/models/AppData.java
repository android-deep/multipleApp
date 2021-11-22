package com.ft.mapp.home.models;

import android.graphics.drawable.Drawable;

/**
 *
 */

public interface AppData {

    boolean isLoading();

    boolean isFirstOpen();

    Drawable getIcon();

    String getName();

    String getPackageName();

    int getAppId();

    int getUserId();

    FakeAppInfo getFakeAppInfo();

    boolean canReorder();

    boolean canLaunch();

    boolean canDelete();

    boolean canCreateShortcut();
}
