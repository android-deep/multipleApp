package com.ft.mapp.ad.reward;

import android.app.Activity;
import android.util.Log;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.ft.mapp.VApp;
import com.ft.mapp.ad.base.RewardAd;
import com.ft.mapp.ad.ttads.TTAdManagerHolder;

public class TTRewardAd extends RewardAd {
    private TTAdNative mTTAdNative;
    private TTRewardVideoAd mttRewardVideoAd;
    private Activity activity;

    public TTRewardAd() {
        com.bytedance.sdk.openadsdk.TTAdManager ttAdManager = TTAdManagerHolder.get();
        mTTAdNative = ttAdManager.createAdNative(VApp.getApp());
    }

    private void showRewardAd() {
        if (mttRewardVideoAd != null) {
            //step6:在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
            //该方法直接展示广告
            mttRewardVideoAd.showRewardVideoAd(activity);
            mttRewardVideoAd = null;
        } else {
            if (listener!=null){
                listener.onError(-1,"暂无广告");
            }
        }
    }

    private void loadRewardAd(String codeId, boolean showNow) {
        Log.d(TAG, "rv codeId : " + codeId);
        if (codeId.isEmpty()) {
            return;
        }
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setRewardName("金币") //奖励的名称
                .setRewardAmount(0)  //奖励的数量
                .setUserID("user123")//用户id,必传参数
                .setOrientation(TTAdConstant.VERTICAL)
                .build();
        //step5:请求广告
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                //TToast.show(RewardVideoActivity.this, message);
                if (listener != null) {
                    listener.onError(code, message);
                }
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
                //TToast.show(RewardVideoActivity.this, "rewardVideoAd video cached");
                Log.i(TAG,"video cached");
            }

            @Override
            public void onRewardVideoCached(TTRewardVideoAd ttRewardVideoAd) {

            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                //TToast.show(RewardVideoActivity.this, "rewardVideoAd loaded 广告类型：" + getAdType(ad.getRewardVideoAdType()));
                mttRewardVideoAd = ad;
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        //TToast.show(RewardVideoActivity.this, "rewardVideoAd show");
                        if (listener != null) {
                            listener.onAdShow();
                        }
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        //TToast.show(RewardVideoActivity.this, "rewardVideoAd bar click");
                        if (listener != null) {
                            listener.onAdClicked();
                        }
                    }

                    @Override
                    public void onAdClose() {
                        // 预加载下一个激励视频广告
                        loadRewardAd(codeId, false);
                        if (listener != null) {
                            listener.onAdClose();
                        }
                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {
                        //TToast.show(RewardVideoActivity.this, "rewardVideoAd complete");
                    }

                    @Override
                    public void onVideoError() {
                        //TToast.show(RewardVideoActivity.this, "rewardVideoAd error");
                        if (listener != null) {
                            listener.onError(-1, "广告加载失败");
                        }
                    }

                    @Override
                    public void onRewardVerify(boolean b, int i, String s, int i1, String s1) {
                        if (listener != null && b) {
                            listener.onRewardVerify();
                        }
                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
//                    @Override
//                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
//                        //TToast.show(RewardVideoActivity.this, "verify:" + rewardVerify + " amount:" + rewardAmount +
//                        //        " name:" + rewardName);
//                    }

                    @Override
                    public void onSkippedVideo() {
                        //TToast.show(RewardVideoActivity.this, "rewardVideoAd has onSkippedVideo");
                    }
                });
                if (showNow) {
                    showRewardAd();
                }
            }
        });
    }

    @Override
    public void loadReward(Activity activity, String adCode) {
        this.activity = activity;
        loadRewardAd(adCode, true);
    }
}
