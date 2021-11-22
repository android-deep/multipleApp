package com.ft.mapp.ad.banner;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.ft.mapp.ad.base.BannerAd;
import com.ft.mapp.ad.ttads.TTAdManagerHolder;
import com.ft.mapp.utils.UIUtils;

import java.util.List;

public class FTttBannerAd extends BannerAd {

    private final TTAdNative mTTAdNative;
    private Context mContext;
    private ViewGroup bannerContainer;
    private String adCode;
    private TTNativeExpressAd mTTAd;

    public FTttBannerAd(Context mContext) {
        this.mContext = mContext;
        mTTAdNative = TTAdManagerHolder.get().createAdNative(mContext);
    }

    private void loadBannerAd() {
        float screenWidthDp = UIUtils.getScreenWidthDp(mContext);
        float height = screenWidthDp / 3f;
        //设置广告参数
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adCode) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(3) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(screenWidthDp, height) //期望个性化模板广告view的size,单位dp
                .setImageAcceptedSize(640, 260)//这个参数设置即可，不影响个性化模板广告的size
                .build();
        //加载广告
        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.i("banner-", "onError --" + code + "--" + message);
                if (listener != null) {
                    listener.onError(code, message);
                }
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0) {
                    return;
                }
                mTTAd = ads.get(0);
                mTTAd.setSlideIntervalTime(10 * 1000);//设置轮播间隔 ms,不调用则不进行轮播展示
                bindAdListener(mTTAd);
                mTTAd.render();//调用render开始渲染广告
            }
        });

    }

    //绑定广告行为
    private void bindAdListener(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
//                TToast.show(mContext, "广告被点击");
                if (listener != null) {
                    listener.onAdClicked();
                }
            }

            @Override
            public void onAdShow(View view, int type) {
//                TToast.show(mContext, "广告展示");
                if (listener != null) {
                    listener.onAdShow(view);
                }
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
//                Log.e("ExpressView","render fail:"+(System.currentTimeMillis() - startTime));
//                TToast.show(mContext, msg+" code:"+code);
                Log.i("banner-", "onRenderFail");
                if (listener != null) {
                    listener.onError(code, msg);
                }
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                //返回view的宽高 单位 dp
//                TToast.show(mContext, "渲染成功");
                Log.i("banner-", "onRenderSuccess");
                //在渲染成功回调时展示广告，提升体验
                if (listener != null) {
                    listener.onBannerAdLoad(view);
                }
                if (bannerContainer != null) {
                    bannerContainer.removeAllViews();
                    bannerContainer.addView(view);
                } else {
                    throw new RuntimeException("banner's container must not be null!");
                }
            }
        });
    }

    @Override
    public void loadBanner(ViewGroup bannerContainer, String adCode) {
        this.bannerContainer = bannerContainer;
        this.adCode = adCode;
        loadBannerAd();
    }
}
