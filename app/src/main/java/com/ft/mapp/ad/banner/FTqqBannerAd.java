package com.ft.mapp.ad.banner;

import android.app.Activity;
import android.graphics.Point;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ft.mapp.ad.base.BannerAd;
import com.ft.mapp.utils.UIUtils;
import com.qq.e.ads.nativ.express2.AdEventListener;
import com.qq.e.ads.nativ.express2.MediaEventListener;
import com.qq.e.ads.nativ.express2.NativeExpressAD2;
import com.qq.e.ads.nativ.express2.NativeExpressADData2;
import com.qq.e.ads.nativ.express2.VideoOption2;
import com.qq.e.comm.util.AdError;

import java.util.List;

public class FTqqBannerAd extends BannerAd implements NativeExpressAD2.AdLoadListener{

    private Activity mContext;
    private ViewGroup bannerContainer;
    private String adCode;
    private NativeExpressAD2 mNativeExpressAD2;
    private NativeExpressADData2 mNativeExpressADData2;

    public FTqqBannerAd(Activity mContext) {
        this.mContext = mContext;
    }

    private void loadBanner() {
        mNativeExpressAD2 = new NativeExpressAD2(mContext,adCode,this);
        loadAd();
    }

    /**
     * banner2.0规定banner宽高比应该为6.4:1 , 开发者可自行设置符合规定宽高比的具体宽度和高度值
     *
     * @return
     */
    private FrameLayout.LayoutParams getUnifiedBannerLayoutParams() {
        Point screenSize = new Point();
        mContext.getWindowManager().getDefaultDisplay().getSize(screenSize);
        return new FrameLayout.LayoutParams(screenSize.x,  Math.round(screenSize.x / 3F));
    }

    @Override
    public void onNoAD(AdError adError) {
        Log.i(TAG, "onNoAD"+"--code="+adError.getErrorCode()+"---adMsg = "+adError.getErrorMsg());
        if (listener!=null){
            listener.onError(adError.getErrorCode(),adError.getErrorMsg());
        }
    }

    @Override
    public void loadBanner(ViewGroup bannerContainer, String adCode) {
        this.bannerContainer = bannerContainer;
        this.adCode = adCode;
        loadBanner();
    }

    // 加载广告，设置条件
    private void loadAd() {
        float screenWidthDp = UIUtils.getScreenWidthDp(mContext);
        float height = screenWidthDp / 3f;
        mNativeExpressAD2.setAdSize((int)screenWidthDp, (int)height); // 单位dp

        // 如果您在平台上新建原生模板广告位时，选择了支持视频，那么可以进行个性化设置（可选）
        VideoOption2.Builder builder = new VideoOption2.Builder();

        /**
         * 如果广告位支持视频广告，强烈建议在调用loadData请求广告前设置setAutoPlayPolicy，有助于提高视频广告的eCPM值 <br/>
         * 如果广告位仅支持图文广告，则无需调用
         */
        builder.setAutoPlayPolicy(VideoOption2.AutoPlayPolicy.WIFI) // WIFI 环境下可以自动播放视频
                .setAutoPlayMuted(true) // 自动播放时为静音
                .setDetailPageMuted(false)  // 视频详情页播放时不静音
                .setMaxVideoDuration(0) // 设置返回视频广告的最大视频时长（闭区间，可单独设置），单位:秒，默认为 0 代表无限制，合法输入为：5<=maxVideoDuration<=60. 此设置会影响广告填充，请谨慎设置
                .setMinVideoDuration(0); // 设置返回视频广告的最小视频时长（闭区间，可单独设置），单位:秒，默认为 0 代表无限制， 此设置会影响广告填充，请谨慎设置
        mNativeExpressAD2.setVideoOption2(builder.build());
        mNativeExpressAD2.loadAd(1);
        destroyAd();
    }

    /**
     *  释放前一个 NativeExpressADData2 的资源
     */
    private void destroyAd() {
        if (mNativeExpressADData2 != null) {
            Log.d(TAG, "destroyAD");
            mNativeExpressADData2.destroy();
        }
    }

    /**
     * 广告加载成功回调
     * @param adDataList
     */
    @Override
    public void onLoadSuccess(List<NativeExpressADData2> adDataList) {
        Log.i(TAG, "onLoadSuccess: size " + adDataList.size());
        // 渲染广告
        renderAd(adDataList);
    }

    /**
     * 渲染广告
     * @param adDataList
     */
    private void renderAd(List<NativeExpressADData2> adDataList) {
        if (adDataList.size() > 0) {
            bannerContainer.removeAllViews();
            mNativeExpressADData2 = adDataList.get(0);
            Log.i(TAG, "renderAd: " + "  eCPM level = " +
                    mNativeExpressADData2.getECPMLevel() + "  Video duration: " + mNativeExpressADData2.getVideoDuration());
            mNativeExpressADData2.setAdEventListener(new AdEventListener() {
                @Override
                public void onClick() {
                    Log.i(TAG, "onClick: " + mNativeExpressADData2);
                    if (listener!=null){
                        listener.onAdClicked();
                    }
                }

                @Override
                public void onExposed() {
                    Log.i(TAG, "onExposed: " + mNativeExpressADData2);
                }

                @Override
                public void onRenderSuccess() {
                    Log.i(TAG, "onRenderSuccess: " + mNativeExpressADData2);
                    bannerContainer.removeAllViews();
                    if (mNativeExpressADData2.getAdView() != null) {
                        bannerContainer.addView(mNativeExpressADData2.getAdView());
                        if (listener!=null){
                            listener.onBannerAdLoad(mNativeExpressADData2.getAdView());
                        }
                    }
                }

                @Override
                public void onRenderFail() {
                    Log.i(TAG, "onRenderFail: " + mNativeExpressADData2);
                }

                @Override
                public void onAdClosed() {
                    Log.i(TAG, "onAdClosed: " + mNativeExpressADData2);
                    bannerContainer.removeAllViews();
                    mNativeExpressADData2.destroy();
                }
            });

            mNativeExpressADData2.setMediaListener(new MediaEventListener() {
                @Override
                public void onVideoCache() {
                    Log.i(TAG, "onVideoCache: " + mNativeExpressADData2);
                }

                @Override
                public void onVideoStart() {
                    Log.i(TAG, "onVideoStart: " + mNativeExpressADData2);
                }

                @Override
                public void onVideoResume() {
                    Log.i(TAG, "onVideoResume: " + mNativeExpressADData2);
                }

                @Override
                public void onVideoPause() {
                    Log.i(TAG, "onVideoPause: " + mNativeExpressADData2);
                }

                @Override
                public void onVideoComplete() {
                    Log.i(TAG, "onVideoComplete: " + mNativeExpressADData2);
                }

                @Override
                public void onVideoError() {
                    Log.i(TAG, "onVideoError: " + mNativeExpressADData2);
                }
            });

            mNativeExpressADData2.render();
        }
    }

}
