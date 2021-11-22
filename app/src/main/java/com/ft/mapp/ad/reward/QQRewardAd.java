package com.ft.mapp.ad.reward;

import android.app.Activity;

import com.ft.mapp.ad.AdUtils;
import com.ft.mapp.ad.base.RewardAd;
import com.ft.mapp.ad.ttads.TTAdManagerHolder;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.comm.util.AdError;
import com.xqb.user.net.engine.StatAgent;
import com.xqb.user.util.UmengStat;

import java.util.Map;

public class QQRewardAd extends RewardAd {

    private RewardVideoAD qqRewardAd;

    @Override
    public void loadReward(Activity activity, String adCode) {
        this.activity = activity;
        loadQQAd(true);
    }

    private void loadQQAd(boolean showNow) {
        // 1.加载广告，先设置加载上下文环境和条件
        // 如果想静音播放，请使用5个参数的构造函数，且volumeOn传false即可
        qqRewardAd = new RewardVideoAD(activity, AdUtils.getQQRewardId(), new RewardVideoADListener() {
            @Override
            public void onADClick() {
                if (listener!=null){
                    listener.onAdClicked();
                }
            }

            @Override
            public void onADClose() {
                if (listener!=null){
                    listener.onAdClose();
                }
            }

            @Override
            public void onADExpose() {

            }

            @Override
            public void onReward(Map<String, Object> map) {
                if (listener!=null){
                    listener.onRewardVerify();
                }
            }

            @Override
            public void onADLoad() {
                if (showNow) {
                    qqRewardAd.showAD(activity);
                }

            }

            @Override
            public void onADShow() {
//                StatAgent.onEvent(activity, UmengStat.REWARD_AD_TYPE, "type", "优量汇");
                if (listener!=null){
                    listener.onAdShow();
                }
            }

            @Override
            public void onError(AdError adError) {
                if (listener!=null){
                    listener.onError(adError.getErrorCode(),adError.getErrorMsg());
                }
            }

            @Override
            public void onVideoCached() {

            }

            @Override
            public void onVideoComplete() {
            }
        });

        qqRewardAd.loadAD();
    }

}
