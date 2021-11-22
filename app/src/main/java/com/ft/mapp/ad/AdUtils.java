package com.ft.mapp.ad;

import com.ft.mapp.VApp;
import com.xqb.user.net.engine.UserAgent;

public class AdUtils {
    private static String bannerId = "945309337";
    private static String bannerNewId = "945712498";

    private static String splashId = "887346045";
    private static String splashNewId = "887417685";
    private static String splashQQId = "9061653179776104";
    private static String splashQQNewId = "7011956139477171";

    private static String rewardId = "945490387";
    private static String rewardNewId = "945712560";
    private static String rewardQQId = "7091656275367398";
    private static String rewardQQNewId = "5061555205961327";

    private static String vipRewardId = "945543705";
    private static String vipRewardNewId = "945712504";
    private static String vipRewardQQId = "9091957265586490";
    private static String vipRewardQQNewId = "1031755215285464";

    public static String getBannerId(){
        String bannerId;
        if (UserAgent.getInstance(VApp.getApp()).isNewUser()){
            bannerId = AdUtils.bannerNewId;
        }else{
            bannerId = AdUtils.bannerId;
        }
        return bannerId;
    }

    public static String getSplashId(){
        String splashId;
        if (UserAgent.getInstance(VApp.getApp()).isNewUser()){
            splashId = AdUtils.splashNewId;
        }else{
            splashId = AdUtils.splashId;
        }
        return splashId;
    }

    public static String getQQSplashId(){
        String splashId;
        if (UserAgent.getInstance(VApp.getApp()).isNewUser()){
            splashId = AdUtils.splashQQNewId;
        }else{
            splashId = AdUtils.splashQQId;
        }
        return splashId;
    }

    public static String getVipRewardId(){
        String vipRewardId;
        if (UserAgent.getInstance(VApp.getApp()).isNewUser()){
            vipRewardId = AdUtils.vipRewardNewId;
        }else{
            vipRewardId = AdUtils.vipRewardId;
        }
        return vipRewardId;
    }

    public static String getQQVipRewardId(){
        String vipRewardId;
        if (UserAgent.getInstance(VApp.getApp()).isNewUser()){
            vipRewardId = AdUtils.vipRewardQQNewId;
        }else{
            vipRewardId = AdUtils.vipRewardQQId;
        }
        return vipRewardId;
    }

    public static String getRewardId(){
        String rewardId;
        if (UserAgent.getInstance(VApp.getApp()).isNewUser()){
            rewardId = AdUtils.rewardNewId;
        }else{
            rewardId = AdUtils.rewardId;
        }
        return rewardId;
    }

    public static String getQQRewardId(){
        String rewardId;
        if (UserAgent.getInstance(VApp.getApp()).isNewUser()){
            rewardId = AdUtils.rewardQQNewId;
        }else{
            rewardId = AdUtils.rewardQQId;
        }
        return rewardId;
    }
}
