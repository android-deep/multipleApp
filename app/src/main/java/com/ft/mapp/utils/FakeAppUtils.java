package com.ft.mapp.utils;

import android.content.Context;

import com.ft.mapp.db.DBManager;
import com.ft.mapp.home.models.FakeAppInfo;

import java.util.HashMap;
import java.util.List;

public class FakeAppUtils {


    public final static HashMap<Integer, FakeAppInfo> fakeAppMap = new HashMap<>();

    public static void init(Context context){
        DBManager.INSTANCE.initDB(context);
    }

    public static List<FakeAppInfo> load(){
        return DBManager.INSTANCE.getDaoSession().getFakeAppInfoDao().loadAll();
    }

    public static FakeAppInfo load(long key){
        return DBManager.INSTANCE.getDaoSession().getFakeAppInfoDao().load(key);
    }

    public static long insert(FakeAppInfo appInfo){
        fakeAppMap.put(appInfo.getAppId(), appInfo);
        return DBManager.INSTANCE.getDaoSession().getFakeAppInfoDao().insertOrReplace(appInfo);
    }

    public static void delete(long key){
        DBManager.INSTANCE.getDaoSession().getFakeAppInfoDao().deleteByKey(key);
    }

}
