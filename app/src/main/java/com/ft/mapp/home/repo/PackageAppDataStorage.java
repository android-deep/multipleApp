package com.ft.mapp.home.repo;

import com.fun.vbox.client.core.VCore;
import com.fun.vbox.remote.InstalledAppInfo;

import java.util.HashMap;
import java.util.Map;

import com.ft.mapp.VApp;
import com.ft.mapp.abs.Callback;
import com.xqb.user.net.engine.VUiKit;
import com.ft.mapp.home.models.PackageAppData;

/**
 *
 *         <p>
 *         Cache the loaded PackageAppData.
 */
public class PackageAppDataStorage {

    private static final PackageAppDataStorage STORAGE = new PackageAppDataStorage();
    private final Map<String, PackageAppData> packageDataMap = new HashMap<>();

    public static PackageAppDataStorage get() {
        return STORAGE;
    }

    public PackageAppData acquire(String packageName) {
        PackageAppData data;
        synchronized (packageDataMap) {
            data = packageDataMap.get(packageName);
            if (data == null) {
                data = loadAppData(packageName);
            }
        }
        return data;
    }

    public void acquire(String packageName, Callback<PackageAppData> callback) {
        VUiKit.defer()
                .when(() -> acquire(packageName))
                .done(callback::callback);
    }

    private PackageAppData loadAppData(String packageName) {
        InstalledAppInfo setting = VCore.get().getInstalledAppInfo(packageName, 0);
        if (setting != null) {
            PackageAppData data = new PackageAppData(VApp.getApp(), setting);
            synchronized (packageDataMap) {
                packageDataMap.put(packageName, data);
            }
            return data;
        }
        return null;
    }

}
