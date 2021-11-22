package com.xqb.user.net.engine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.xqb.user.BuildConfig;
import com.xqb.user.bean.UserInfo;
import com.xqb.user.bean.VersionBean;
import com.xqb.user.bean.VipProductInfo;
import com.xqb.user.net.lisenter.ApiCallback;
import com.xqb.user.util.GsonUtil;
import com.xqb.user.util.StatUtils;
import com.xqb.user.util.UserSharePref;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

import retrofit2.http.POST;


public class UserAgent {

    @SuppressLint("StaticFieldLeak")
    private static volatile UserAgent sInstance;
    private UserInfo mUserInfo;
    private Context mContext;
    private VipProductInfo mProductInfo;
    private VersionBean mVersionBean;
    public static final String ACTIVATION_CODE = "";
    //    public static String actUrl = "https://s.click.taobao.com/xe3yyuu";
//    public static String actUrl = "https://s.click.taobao.com/ft3Gjuu";
    public static String actUrl = "https://jingpage.com/#/nineMail?app_key=erpjcd";
//    public static String actUrl = "https://s.click.taobao.com/nrjeuuu";

    public static UserAgent getInstance(Context context) {
        if (sInstance == null) {
            synchronized (UserAgent.class) {
                if (sInstance == null) {
                    sInstance = new UserAgent(context);
                }
            }
        }
        return sInstance;
    }

    private UserAgent(Context context) {
        mContext = context;
    }

    public void init() {
//        loadUserInfo();
        loadProductInfo();
        loadVersionInfo();
    }

    public String getAndroidId() {
        String androidId = UserSharePref.getInstance(mContext).getString(UserSharePref.KEY_ANDROID_ID);
        if (TextUtils.isEmpty(androidId)) {
            @SuppressLint("HardwareIds") String uuid = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (TextUtils.isEmpty(uuid)) {
                uuid = UUID.randomUUID().toString();
            }
            UserSharePref.getInstance(mContext).putString(UserSharePref.KEY_ANDROID_ID, uuid);
            return uuid;
        }
        return androidId;
    }

    public void clearUserInfo() {
        mUserInfo = null;
        UserSharePref.getInstance(mContext).putString(UserSharePref.KEY_USER_INFO, "");
    }

    private void loadUserInfo() {
        if (mUserInfo != null && !TextUtils.isEmpty(mUserInfo.userId)) {
            return;
        }
        String userInfo = UserSharePref.getInstance(mContext).getString(UserSharePref.KEY_USER_INFO);
        mUserInfo = GsonUtil.gson2Bean(userInfo, UserInfo.class);
//        if (mUserInfo == null || TextUtils.isEmpty(mUserInfo.userId)) {
//            new ApiServiceDelegate(mContext).register("", "", new ApiCallback() {
//                @Override
//                public void onSuccess() {
//                    if (!TextUtils.isEmpty(ACTIVATION_CODE) &&
//                            mUserInfo != null && !ACTIVATION_CODE.equals(mUserInfo.groupid)) {
//                        new ApiServiceDelegate(mContext).activation(ACTIVATION_CODE, null);
//                    }
//                }
//
//                @Override
//                public void onFail(String msg) {
//                }
//            });
//        }
    }

    public UserInfo getUserInfo() {
        if (mUserInfo == null || TextUtils.isEmpty(mUserInfo.userId)) {
            loadUserInfo();
        }
        return mUserInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
    }

    public boolean isVipUser() {
        if (BuildConfig.DEBUG){
            return false;
        }
        if (getUserInfo() == null) {
            return false;
        }
        return mUserInfo.isVip && (mUserInfo.expireTime >= mUserInfo.responseTime);
    }

    private void loadProductInfo() {
        if (mProductInfo != null) {
            return;
        }
        String productInfo = UserSharePref.getInstance(mContext).getString(UserSharePref.KEY_PRODUCT_INFO);
        mProductInfo = GsonUtil.gson2Bean(productInfo, VipProductInfo.class);
    }

    public VipProductInfo getProductInfo() {
        if (mProductInfo == null) {
            loadProductInfo();
        }
        return mProductInfo;
    }

    public void setProductInfo(VipProductInfo info) {
        mProductInfo = info;
    }

    private void loadVersionInfo() {
        if (mVersionBean != null) {
            return;
        }
        String versionInfo = UserSharePref.getInstance(mContext).getString(UserSharePref.KEY_VERION_INFO);
        mVersionBean = GsonUtil.gson2Bean(versionInfo, VersionBean.class);
        if (mVersionBean != null) {
            AdAgent.init(mContext, mVersionBean);
        }
    }

    public VersionBean getVersionBean() {
        if (mVersionBean == null) {
            loadVersionInfo();
        }
        return mVersionBean;
    }

    public void setVersionBean(VersionBean versionBean) {
        mVersionBean = versionBean;
    }

    public boolean isAdOpen() {
        boolean isShowAd;
        VersionBean versionBean = getVersionBean();
        if (versionBean != null) {
            if (!TextUtils.isEmpty(versionBean.version_audit)) {
                //是否为审核版本
                isShowAd = !TextUtils.equals(versionBean.version_audit, StatUtils.getVersionName(mContext));
            }else{
                isShowAd = true;
            }
        } else {
            isShowAd = false;
        }
        if (!isShowAd) {
            return false;
        }
        return !isVipUser();
    }

    public boolean isPayVipUser() {
        return isVipUser() && (mUserInfo.hasBuy == 1);
    }

    public boolean isVirtualLocationOn(){
       boolean bitOpen;
        VersionBean versionBean = getVersionBean();
        if (versionBean != null) {
            if (!TextUtils.isEmpty(versionBean.ad_virtual_version)) {
                //是否为64位审核版本
                bitOpen = !TextUtils.equals(versionBean.ad_virtual_version, StatUtils.getVersionName(mContext));
            }else{
                bitOpen = true;
            }
        } else {
            bitOpen = false;
        }
        return bitOpen;
    }

    public boolean isLotteryOn() {
        if (!isAdOpen()) {
            return false;
        }
        if (mVersionBean.ad_switch == null) {
            return false;
        }
        return mVersionBean.ad_switch.lottery != null && mVersionBean.ad_switch.lottery != 0;
    }

    public boolean isRewardOn() {
        if (!isAdOpen()) {
            return false;
        }
        if (mVersionBean.ad_switch == null) {
            return false;
        }
        return mVersionBean.ad_switch.reward != null && mVersionBean.ad_switch.reward != 0;
    }

    public boolean isBannerOn() {
        if (!isAdOpen()) {
            return false;
        }
        if (mVersionBean.ad_switch == null) {
            return false;
        }
        return mVersionBean.ad_switch.banner != null && mVersionBean.ad_switch.banner != 0;
    }

    public boolean isVideoOn() {
        if (!isAdOpen()) {
            return false;
        }
        if (mVersionBean.ad_switch == null) {
            return false;
        }
        return mVersionBean.ad_switch.video != null && mVersionBean.ad_switch.video != 0;
    }

    public boolean isDrawOn() {
        if (!isAdOpen()) {
            return false;
        }
        if (mVersionBean.ad_switch == null) {
            return false;
        }
        return mVersionBean.ad_switch.draw != null && mVersionBean.ad_switch.draw != 0;
    }

    public boolean isMiniGameOn() {
//        if (!isAdOpen()) {
//            return false;
//        }
        if (getVersionBean() == null) {
            return false;
        }
        if (mVersionBean.ad_switch == null) {
            return false;
        }
        return mVersionBean.ad_switch.minigame != null && mVersionBean.ad_switch.minigame != 0;
    }

    public boolean isTopOn() {
        if (!isAdOpen()) {
            return false;
        }
        if (mVersionBean.ad_switch == null) {
            return false;
        }
        return mVersionBean.ad_switch.top != null && mVersionBean.ad_switch.top != 0;
    }

    public boolean isThirdOn() {
        if (getVersionBean() == null) {
            return false;
        }
        return mVersionBean.thirdAD != null && mVersionBean.thirdAD != 0;
    }

    public boolean isSplashOn() {
        if (getVersionBean() == null) {
            return false;
        }
        if (mVersionBean.ad_switch == null) {
            return false;
        }
        return mVersionBean.ad_switch.splash != null && mVersionBean.ad_switch.splash != 0;
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

    //是否新用户
    public String userType() {
        if (mUserInfo == null) {
            return "new";
        } else {
            String responseTime = sdf.format(mUserInfo.responseTime);
            String registerTime = sdf.format(mUserInfo.registerTime);
            return responseTime.equals(registerTime) ? "new" : "old";
        }
    }

    public boolean isNewUser(){
        return userType().equals("new");
    }

}
