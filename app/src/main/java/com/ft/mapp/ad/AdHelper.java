package com.ft.mapp.ad;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.ft.mapp.VApp;
import com.ft.mapp.ad.base.RewardAdListener;
import com.ft.mapp.ad.base.SplashAdListener;
import com.ft.mapp.ad.reward.QQRewardAd;
import com.ft.mapp.ad.reward.TTRewardAd;
import com.ft.mapp.ad.splash.FTqqSplashAd;
import com.ft.mapp.ad.splash.FTttSplashAd;
import com.ft.mapp.ad.ttads.TTAdManagerHolder;
import com.ft.mapp.utils.ToastUtil;
import com.xqb.user.bean.VersionBean;
import com.xqb.user.net.engine.UserAgent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class AdHelper {

    private Activity activity;
    private static Random random = new Random();

    public static boolean dispatchTTAd() {
        int choiceThisTime;
        if (UserAgent.getInstance(VApp.getApp()).isNewUser()) {
            if (newRateMap == null || newRateMap.size() == 0) {
                return true;
            }
            choiceThisTime = random.nextInt(newTotalRange);
            String targetAdPlatform = "newTT";
            Set<Map.Entry<Integer, String>> entries = newRateMap.entrySet();
            for (Map.Entry<Integer, String> entry : entries) {
                if (choiceThisTime <= entry.getKey()) {
                    targetAdPlatform = entry.getValue();
                    Log.i("ADHELPER", choiceThisTime + "new choice platform = " + targetAdPlatform);
                    break;
                }
            }
            return "newTT".equals(targetAdPlatform);
        } else {
            if (rateMap == null || rateMap.size() == 0) {
                return true;
            }
            choiceThisTime = random.nextInt(totalRange);
            String targetAdPlatform = "tt";
            Set<Map.Entry<Integer, String>> entries = rateMap.entrySet();
            for (Map.Entry<Integer, String> entry : entries) {
                if (choiceThisTime <= entry.getKey()) {
                    targetAdPlatform = entry.getValue();
                    Log.i("ADHELPER", choiceThisTime + "choice platform = " + targetAdPlatform);
                    break;
                }
            }
            return "tt".equals(targetAdPlatform);
        }
    }

    //旧用户
    private static LinkedHashMap<Integer, String> rateMap = new LinkedHashMap<>();
    private static int totalRange = 0;

    //新用户
    private static LinkedHashMap<Integer, String> newRateMap = new LinkedHashMap<>();
    private static int newTotalRange = 0;

    //广告权重初始化
    public static void initRate() {
        VersionBean versionBean = UserAgent.getInstance(VApp.getApp()).getVersionBean();
        if (versionBean == null) {
            return;
        }
        List<VersionBean.AdWeights> ad_weights = versionBean.getAd_weights();
        if (ad_weights == null) {
            return;
        }
        for (VersionBean.AdWeights ad_weight : ad_weights) {
            if (ad_weight.weight <= 0) {
                continue;
            }
            if (ad_weight.keyname.contains("new")) {
                newTotalRange += ad_weight.weight;
                newRateMap.put(newTotalRange, ad_weight.keyname);
            } else {
                totalRange += ad_weight.weight;
                rateMap.put(totalRange, ad_weight.keyname);
            }
        }
    }

    public AdHelper(Activity activity) {
        this.activity = activity;
    }

    public void showRewardAd(RewardAdListener listener) {
        if (dispatchTTAd()) {
            loadTTAd(false, true, listener);
        } else {
            loadQQAd(false, true, listener);
        }
    }

    public void showVipRewardAd(RewardAdListener listener) {
        if (dispatchTTAd()) {
            loadTTAd(true, true, listener);
        } else {
            loadQQAd(true, true, listener);
        }
    }

    /**
     * 加载穿山甲广告
     *
     * @param checkOther 是否加载失败后选择另外一个平台
     */
    private void loadTTAd(boolean isVipFunction, boolean checkOther, RewardAdListener listener) {
        if (activity == null) {
            return;
        }
        TTRewardAd ttRewardAd = new TTRewardAd();
        ttRewardAd.loadReward(activity, isVipFunction ? AdUtils.getVipRewardId() : AdUtils.getRewardId());
        ttRewardAd.setAdListener(new RewardAdListener() {
            @Override
            public void onError(int code, String msg) {
                if (checkOther) {
                    loadQQAd(isVipFunction, false, listener);
                } else {
                    listener.onError(code, msg);
                }
            }

            @Override
            public void onTimeout() {
                listener.onTimeout();
            }

            @Override
            public void onAdClicked() {
                listener.onAdClicked();
            }

            @Override
            public void onAdShow() {
                listener.onAdShow();
            }

            @Override
            public void onRewardVerify() {
                listener.onRewardVerify();
            }

            @Override
            public void onAdSkip() {
                listener.onAdSkip();
            }

            @Override
            public void onAdClose() {
                listener.onAdClose();
            }
        });
    }

    /**
     * 加载优量汇广告
     *
     * @param checkOther 是否加载失败后选择另外一个平台
     */
    private void loadQQAd(boolean isVipFunction, boolean checkOther, RewardAdListener listener) {
        if (activity == null) {
            return;
        }
        QQRewardAd qqRewardAd = new QQRewardAd();
        qqRewardAd.loadReward(activity, isVipFunction ? AdUtils.getQQVipRewardId() : AdUtils.getQQRewardId());
        qqRewardAd.setAdListener(new RewardAdListener() {
            @Override
            public void onError(int code, String msg) {
                if (checkOther) {
                    loadTTAd(isVipFunction, false, listener);
                } else {
                    listener.onError(code, msg);
                }
            }

            @Override
            public void onTimeout() {
                listener.onTimeout();
            }

            @Override
            public void onAdClicked() {
                listener.onAdClicked();
            }

            @Override
            public void onAdShow() {
                listener.onAdShow();
            }

            @Override
            public void onRewardVerify() {
                listener.onRewardVerify();
            }

            @Override
            public void onAdSkip() {
                listener.onAdSkip();
            }

            @Override
            public void onAdClose() {
                listener.onAdClose();
            }
        });
    }

    /**
     * 加载开屏广告
     */
    public void showSplashAd(SplashAdListener listener) {
//        loadTTSplashAd(true, listener);
        if (dispatchTTAd()) {
            loadTTSplashAd(true, listener);
        } else {
            loadQQSplashAd(true, listener);
        }
    }

    private void loadTTSplashAd(boolean tryOther, SplashAdListener listener) {
        if (activity == null) {
            return;
        }
        FTttSplashAd ttSplashAd = new FTttSplashAd(activity);
        ttSplashAd.setAdListener(new SplashAdListener() {
            @Override
            public void onError(int code, String msg) {
                Log.i("ADHELPER", "code = " + code + "--msg = " + msg);
                if (tryOther) {
                    loadQQSplashAd(false, listener);
                } else {
                    listener.onError(code, msg);
                }
            }

            @Override
            public void onTimeout() {
                listener.onTimeout();
            }

            @Override
            public void onSplashAdLoad(View adView) {
                listener.onSplashAdLoad(adView);
            }

            @Override
            public void onAdClicked() {
                listener.onAdClicked();
            }

            @Override
            public void onAdShow(View adView) {
                listener.onAdShow(adView);
            }

            @Override
            public void onAdSkip() {
                listener.onAdSkip();
            }

            @Override
            public void onAdClose() {
                listener.onAdClose();
            }
        });
    }

    private void loadQQSplashAd(boolean tryOther, SplashAdListener listener) {
        if (activity == null) {
            return;
        }
        FTqqSplashAd qqSplashAd = new FTqqSplashAd(activity);
        qqSplashAd.setAdListener(new SplashAdListener() {
            @Override
            public void onError(int code, String msg) {
                if (tryOther) {
                    loadTTSplashAd(false, listener);
                } else {
                    listener.onError(code, msg);
                }
            }

            @Override
            public void onTimeout() {
                listener.onTimeout();
            }

            @Override
            public void onSplashAdLoad(View adView) {
                listener.onSplashAdLoad(adView);
            }

            @Override
            public void onAdClicked() {
                listener.onAdClicked();
            }

            @Override
            public void onAdShow(View adView) {
                listener.onAdShow(adView);
            }

            @Override
            public void onAdSkip() {
                listener.onAdSkip();
            }

            @Override
            public void onAdClose() {
                listener.onAdClose();
            }
        });
    }

}
