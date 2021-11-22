package com.ft.mapp.ad.splash;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.MainThread;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTSplashAd;

import com.ft.mapp.ad.AdUtils;
import com.ft.mapp.ad.base.SplashAd;
import com.ft.mapp.ad.ttads.TTAdManagerHolder;

public class FTttSplashAd extends SplashAd {

    private TTAdNative mTTAdNative;

    public FTttSplashAd(Context context) {
        if (mTTAdNative == null) {
            TTAdManager ttAdManager = TTAdManagerHolder.get();
            if (ttAdManager == null) {
                listener.onError(0, "穿山甲广告未初始化");
                return;
            }
            mTTAdNative = ttAdManager.createAdNative(context);
        }
        //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot;

        adSlot = new AdSlot.Builder()
                .setCodeId(AdUtils.getSplashId())
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .build();

        //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            @MainThread
            public void onError(int code, String message) {
//                Log.d(TAG, String.valueOf(message));
                listener.onError(code, message);
            }

            @Override
            @MainThread
            public void onTimeout() {
                listener.onTimeout();
            }

            @Override
            public void onSplashAdLoad(TTSplashAd ad) {
                Log.d(TAG, "开屏广告请求成功");
                if (ad == null) {
                    return;
                }
                listener.onSplashAdLoad(ad.getSplashView());
//                StatAgent.onEvent(context, UmengStat.SPLASH_AD_LOAD, "type", "穿山甲");
                //设置SplashView的交互监听器
                ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        Log.d(TAG, "onAdClicked");
                        listener.onAdClicked();
//                        StatAgent.onEvent(context, UmengStat.SPLASH_AD_CLICK, "type", "穿山甲");
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        Log.d(TAG, "onAdShow");
                        listener.onAdShow(view);
//                        StatAgent.onEvent(context, UmengStat.SPLASH_AD_SHOW, "type", "穿山甲");
                    }

                    @Override
                    public void onAdSkip() {
                        Log.d(TAG, "onAdSkip");
                        listener.onAdSkip();
                    }

                    @Override
                    public void onAdTimeOver() {
                        Log.d(TAG, "onAdTimeOver");
                        listener.onAdClose();
                    }
                });

            }

        }, AD_TIME_OUT);
    }

}
