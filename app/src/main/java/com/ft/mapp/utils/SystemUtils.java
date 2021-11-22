package com.ft.mapp.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class SystemUtils {
    /**
     * @param context
     * @param packagename
     * @return
     */
    public static boolean isAppInstalled(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename,  PackageManager.GET_UNINSTALLED_PACKAGES);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            //System.out.println("没有安装");
            return false;
        } else {
            //System.out.println("已经安装");
            return true;
        }
    }
}
