package com.ft.mapp.ad.splash;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ft.mapp.R;
import com.ft.mapp.ad.AdUtils;
import com.ft.mapp.ad.base.SplashAd;
import com.ft.mapp.ad.ttads.TTAdManagerHolder;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.xqb.user.net.engine.StatAgent;
import com.xqb.user.util.UmengStat;

public class FTqqSplashAd extends SplashAd implements SplashADListener {

    private SplashAD splashAD;
    private Activity mActivity;
    private View adView;
    private TextView skipView;

    public FTqqSplashAd(Activity context) {
        this.mActivity = context;
        generateAdView();
    }

    private void generateAdView() {
        adView = View.inflate(mActivity, R.layout.layout_qq_splash, null);
        ViewGroup container = adView.findViewById(R.id.splash_container);
        skipView = adView.findViewById(R.id.skip_view);
        fetchSplashAD(mActivity, container, skipView, AdUtils.getQQSplashId());
    }

    /**
     * 拉取开屏广告，开屏广告的构造方法有3种，详细说明请参考开发者文档。
     *
     * @param activity      展示广告的activity
     * @param adContainer   展示广告的大容器
     * @param skipContainer 自定义的跳过按钮：传入该view给SDK后，SDK会自动给它绑定点击跳过事件。SkipView的样式可以由开发者自由定制，其尺寸限制请参考activity_splash.xml或者接入文档中的说明。
     * @param posId         广告位ID
     */
    private void fetchSplashAD(Activity activity, ViewGroup adContainer, View skipContainer, String posId) {
        splashAD = new SplashAD(activity, skipContainer, posId, this, AD_TIME_OUT);
        splashAD.fetchAndShowIn(adContainer);
    }

    @Override
    public void onADClicked() {
//        StatAgent.onEvent(mActivity, UmengStat.SPLASH_AD_CLICK, "type", "优量汇");
        listener.onAdClicked();
    }

    @Override
    public void onADDismissed() {
        listener.onAdClose();
    }

    @Override
    public void onADExposure() {
        Log.i("ftqqsplash","onADExposure");
    }

    @Override
    public void onADLoaded(long l) {
//        StatAgent.onEvent(mActivity, UmengStat.SPLASH_AD_LOAD, "type", "优量汇");
        listener.onSplashAdLoad(adView);
    }

    @Override
    public void onADPresent() {
        Log.i("ftqqsplash","onADPresent");
//        StatAgent.onEvent(mActivity, UmengStat.SPLASH_AD_SHOW, "type", "优量汇");
        listener.onAdShow(adView);
    }

    @Override
    public void onADTick(long l) {
        int time = (int) (l / 1000);
        skipView.setText("跳过(" + time + "s)");
    }

    @Override
    public void onNoAD(AdError adError) {
        listener.onError(adError.getErrorCode(), adError.getErrorMsg());
    }
}
