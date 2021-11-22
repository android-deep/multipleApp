package com.xqb.user.net.engine;

import android.content.Context;

import com.xqb.user.bean.VersionBean;

import java.util.HashMap;
import java.util.Map;

public class AdAgent {

//    act_home_banner-----首页banner
//    act_home_item--------首页列表项
//    act_home_float---------首页浮标
//    act_home_tab-----------首页底部导航
//    act_home_dialog--------首页弹窗
//    act_me_banner-------个人中心banner

    private static final String ACT_HOME_BANNER = "act_home_banner";
    private static final String ACT_HOME_ITEM = "act_home_item";
    private static final String ACT_HOME_FLOAT = "act_home_float";
    private static final String ACT_HOME_TAB = "act_home_tab";
    private static final String ACT_HOME_DIALOG = "act_home_dialog";
    private static final String ACT_HOME_TOP = "act_home_top";
    private static final String ACT_ME_BANNER = "act_me_banner";

    private static Context mContext;

    private static Map<String, VersionBean.AdActivity> adMap = new HashMap<>();

    public synchronized static void init(Context context, VersionBean mVersionBean) {
        mContext = context;
        if (mVersionBean != null && mVersionBean.adAct != null) {
            for (VersionBean.AdActivity adActivity : mVersionBean.adAct) {
                adMap.put(adActivity.keyname, adActivity);
            }
        }
    }

    public static VersionBean.AdActivity loadHomeBannerAct() {
        return adMap.get(ACT_HOME_BANNER);
    }

    public static VersionBean.AdActivity loadHomeTabAct() {
        return adMap.get(ACT_HOME_TAB);
    }

    public static VersionBean.AdActivity loadHomeDialogAct() {
        return adMap.get(ACT_HOME_DIALOG);
    }

    public static VersionBean.AdActivity loadHomeItemAct() {
        return adMap.get(ACT_HOME_ITEM);
    }

    public static VersionBean.AdActivity loadMeBannerAct() {
        return adMap.get(ACT_ME_BANNER);
    }

    public static VersionBean.AdActivity loadHomeFloatAct() {
        return adMap.get(ACT_HOME_FLOAT);
    }

    public static VersionBean.AdActivity loadHomeTopAct() {
        return adMap.get(ACT_HOME_TOP);
    }

    public static boolean actHomeBannerOn() {
        return checkActOn(ACT_HOME_BANNER);
    }

    public static boolean actHomeDialogOn() {
        return checkActOn(ACT_HOME_DIALOG);
    }

    public static boolean actHomeTabOn() {
        return checkActOn(ACT_HOME_TAB);
    }

    public static boolean actHomeItemOn() {
        return checkActOn(ACT_HOME_ITEM);
    }

    public static boolean actMeBannerOn() {
        return checkActOn(ACT_ME_BANNER);
    }

    public static boolean actHomeFloatOn() {
        return checkActOn(ACT_HOME_FLOAT);
    }

    public static boolean actHomeTopOn() {
        return checkActOn(ACT_HOME_TOP);
    }

    private static boolean checkActOn(String actType) {
        VersionBean.AdActivity adActivity = adMap.get(actType);
        if (adActivity == null) {
            return false;
        }
        return adActivity.isOpen == 1 && UserAgent.getInstance(mContext).isAdOpen();
    }

}
