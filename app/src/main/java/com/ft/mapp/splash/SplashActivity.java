package com.ft.mapp.splash;

import android.app.Notification;
import android.graphics.Point;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.ft.mapp.BuildConfig;
import com.ft.mapp.VApp;
import com.ft.mapp.VCommends;
import com.ft.mapp.ad.AdHelper;
import com.ft.mapp.ad.base.SplashAdListener;
import com.ft.mapp.ad.ttads.TTAdManagerHolder;
import com.ft.mapp.dialog.LiabilityDialog;
import com.ft.mapp.home.HomeActivity;
import com.ft.mapp.utils.AppSharePref;
import com.ft.mapp.utils.CommonUtil;
import com.ft.mapp.utils.ToastUtil;
import com.ft.mapp.utils.UIUtils;
import com.ft.mapp.R;
import com.ft.mapp.abs.ui.VActivity;
import com.ipaynow.plugin.log.LogUtils;
import com.liulishuo.filedownloader.FileDownloader;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.viewpagerindicator.CirclePageIndicator;
import com.xqb.user.net.engine.ApiServiceDelegate;
import com.xqb.user.net.engine.UserAgent;
import com.xqb.user.net.engine.VUiKit;
import com.fun.vbox.client.core.VCore;
import com.jaeger.library.StatusBarUtil;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import jonathanfinerty.once.Once;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.xqb.user.net.lisenter.ApiCallback;
import com.xqb.user.util.StatUtils;
import com.xqb.user.util.UserSharePref;
//import com.yilan.sdk.ui.YLUIInit;

import java.util.ArrayList;
import java.util.Locale;

public class SplashActivity extends VActivity {
    private static final String TAG = "SplashActivity";
    //    private boolean mIsExpress = false; //是否请求模板广告
//    private TTAdNative mTTAdNative;
    private FrameLayout mSplashContainer;
    private TextView mTxSplashLogo;
    private ImageView ivSplash;
    private ViewPager viewPager;
    private CirclePageIndicator indicator;
    private TextView tvSkip;
    private ImageView ivFinishGuide;
    private static final int AD_TIME_OUT = 3000;
//    private String TTAdManagerHolder.splashId = "887338054";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        @SuppressWarnings("unused")
        boolean enterGuide = Once.beenDone(Once.THIS_APP_INSTALL, VCommends.TAG_NEW_VERSION);
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTransparent(this);
        setContentView(R.layout.activity_splash);
        mSplashContainer = findViewById(R.id.splash_container);
        mTxSplashLogo = findViewById(R.id.splash_logo);
        ivSplash = findViewById(R.id.iv_splash);
//        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);

        VUiKit.defer().when(() -> {
            if (!Once.beenDone("collect_flurry")) {
                Once.markDone("collect_flurry");
            }
            doActionInThread();
        }).done((res) -> {
            showPrivatePolicy();
        });
    }

    private void showPrivatePolicy() {
        if (AppSharePref.getInstance(this).getBoolean(AppSharePref.KEY_AGREE_POLICY)) {
            initSDK();
            return;
        }
        LiabilityDialog dialog = new LiabilityDialog(this);
        dialog.setOnDismissListener(dialog1 -> {
            if (!AppSharePref.getInstance(this).getBoolean(AppSharePref.KEY_AGREE_POLICY)) {
                finish();
            } else {
                initSDK();
            }
        });
        dialog.show();
    }

    private void initSDK() {
        try {
            if (BuildConfig.DEBUG) {
                // 打开统计SDK调试模式
                UMConfigure.setLogEnabled(true);
            }
            JPushInterface.setDebugMode(BuildConfig.DEBUG);
            JPushInterface.init(this);

            BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(this);
            builder.statusBarDrawable = R.drawable.icon_activitiy;
            builder.notificationFlags = Notification.FLAG_AUTO_CANCEL
                    | Notification.FLAG_SHOW_LIGHTS;  //设置为自动消失和呼吸灯闪烁
            builder.notificationDefaults = Notification.DEFAULT_SOUND
                    | Notification.DEFAULT_VIBRATE
                    | Notification.DEFAULT_LIGHTS;  // 设置为铃声、震动、呼吸灯闪烁都要
            JPushInterface.setPushNotificationBuilder(5, builder);

            SDKInitializer.initialize(VApp.getApp());
            SDKInitializer.setCoordType(CoordType.GCJ02);
            UMConfigure.init(this, getString(R.string.umeng_key),
                    StatUtils.getChannel(this),
                    UMConfigure.DEVICE_TYPE_PHONE, null);
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL);

            PlatformConfig.setQQZone("1110620996", "zyQFjLaEIyosuLJ8");
            PlatformConfig.setWeixin("wx3a6e087ddb52dede", "51abd63ea401923c7961ae9eb0844e25");

            UserAgent.getInstance(this).init();

            FileDownloader.setup(this);

//            QbSdk.initX5Environment(getApplicationContext(), null);

//            YLUIInit.getInstance()
//                    .setApplication(VApp.getApp())
////                    .setAccessKey("ylmu0r4ld7i9")//设置accesskey
////                    .setAccessToken("x1whnbrj3fukm8vcil99qux6hnx0ixl2")//设置token
//                    .setAccessKey("ylu4vosr2e1s")//设置accesskey
//                    .setAccessToken("7zz48fh4u5czp8afyx1kdozz0a2hu1tb")//设置token
//                    .setSID(StatUtils.getChannel(this)) //设置渠道号，用于分渠道广告屏蔽
//                    .build();
//            Bxk.init("2e9976ae","751c9bab99c26e6b7ab67dd15065837b");

            TTAdManagerHolder.init(VApp.getApp(), new TTAdSdk.InitCallback() {
                @Override
                public void success() {
                    loadUserInfo();
                    checkUpdate();
                }

                @Override
                public void fail(int i, String s) {
                    loadUserInfo();
                    checkUpdate();
                }
            });
        } catch (Throwable e) {
            Log.i("---", "ignore");
        }
    }

    private boolean userInfoLoadFinish = false;
    private boolean versionLoadFinish = false;

    private void loadUserInfo() {
        new ApiServiceDelegate(this).register("", "", new ApiCallback() {

            @Override
            public void onSuccess() {
                checkUser();
            }

            @Override
            public void onFail(String msg) {
                ToastUtil.show(SplashActivity.this, msg);
                checkUser();
            }
        });
    }

    private void checkUser() {
        boolean firstInstall = UserSharePref.getInstance(this).getBoolean(UserSharePref.KEY_FIRST_INSTALL, true);
        if (firstInstall) {
            showGuide();
            return;
        }
        userInfoLoadFinish = true;
        readyToLoadSplashAd();
    }

    private void checkUpdate() {
        new ApiServiceDelegate(this).adSwitch(new ApiCallback() {
            @Override
            public void onSuccess() {
                versionLoadFinish = true;
                AdHelper.initRate();
                readyToLoadSplashAd();
            }

            @Override
            public void onFail(String errorMsg) {
                versionLoadFinish = true;
                gotoMain();
            }

        });
    }

    private void readyToLoadSplashAd() {
        if (versionLoadFinish && userInfoLoadFinish) {
            if (UserAgent.getInstance(SplashActivity.this).isSplashOn()) {
                loadSplashAd();
            } else {
                adLoadComplete();
            }
        }
    }

    private void showGuide() {
        ViewStub vsGuide = findViewById(R.id.splash_vs_guide);
        if (vsGuide != null) {
            View guideView = vsGuide.inflate();
            viewPager = guideView.findViewById(R.id.splash_vp_guide);
            indicator = guideView.findViewById(R.id.splash_indicator);
            tvSkip = guideView.findViewById(R.id.splash_tv_skip);
            tvSkip.setOnClickListener(view -> {
                guideView.setVisibility(View.GONE);
                userInfoLoadFinish = true;
                readyToLoadSplashAd();
            });
            ivFinishGuide = guideView.findViewById(R.id.splash_iv_btn_guide_finish);
            ivFinishGuide.setOnClickListener(view -> {
                guideView.setVisibility(View.GONE);
                userInfoLoadFinish = true;
                readyToLoadSplashAd();
            });
            guideView.setVisibility(View.VISIBLE);
            Integer[] guideIvRes;
            if (!UserAgent.getInstance(this).isVirtualLocationOn()) {
                guideIvRes = new Integer[]{R.drawable.img_guide_1, R.drawable.img_guide_2, R.drawable.img_guide_3_hw};
            } else {
                guideIvRes = new Integer[]{R.drawable.img_guide_1, R.drawable.img_guide_2, R.drawable.img_guide_3};
            }
            ArrayList<ImageView> guideIvs = new ArrayList<>();
            for (Integer res : guideIvRes) {
                ImageView iv = new ImageView(this);
                iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv.setImageResource(res);
                guideIvs.add(iv);
            }
            viewPager.setAdapter(new PagerAdapter() {
                @Override
                public int getCount() {
                    return guideIvs.size();
                }

                @Override
                public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                    return view == object;
                }

                @NonNull
                @Override
                public Object instantiateItem(@NonNull ViewGroup container, int position) {
                    container.addView(guideIvs.get(position));
                    return guideIvs.get(position);
                }

                @Override
                public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                    container.removeView(guideIvs.get(position));
                }
            });
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 2) {
                        ivFinishGuide.setVisibility(View.VISIBLE);
                    } else {
                        ivFinishGuide.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            indicator.setViewPager(viewPager);
            Point pointBottom = UIUtils.getNavigationBarSize(this);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) indicator.getLayoutParams();
            layoutParams.bottomMargin = pointBottom.y + 20;
            indicator.requestLayout();

            ConstraintLayout.LayoutParams tvParams = (ConstraintLayout.LayoutParams) tvSkip.getLayoutParams();
            float statusBarHeight = UIUtils.getStatusBarHeight(this);
            tvParams.topMargin = (int) statusBarHeight;
            tvSkip.requestLayout();
        } else {
            adLoadComplete();
        }


    }

    /**
     * 加载开屏广告
     */
    private void loadSplashAd() {
        new AdHelper(this).showSplashAd(new SplashAdListener() {
            @Override
            public void onError(int code, String msg) {
                adLoadComplete();
            }

            @Override
            public void onTimeout() {
                adLoadComplete();
            }

            @Override
            public void onSplashAdLoad(View adView) {
                if (mSplashContainer != null && !SplashActivity.this.isFinishing()) {
                    mSplashContainer.setVisibility(View.VISIBLE);
                    mTxSplashLogo.setVisibility(View.GONE);
                    mSplashContainer.removeAllViews();
                    //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕高
                    mSplashContainer.addView(adView);
                    //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                    //ad.setNotAllowSdkCountdown();
                } else {
                    mSplashContainer.setVisibility(View.GONE);
                    mTxSplashLogo.setVisibility(View.VISIBLE);
                    adLoadComplete();
                }
            }

            @Override
            public void onAdClicked() {

            }

            @Override
            public void onAdShow(View adView) {

            }

            @Override
            public void onAdSkip() {
                mTxSplashLogo.setVisibility(View.VISIBLE);
                adLoadComplete();
            }

            @Override
            public void onAdClose() {
                adLoadComplete();
            }
        });
    }

    private boolean adLoadComplete = false;

    private void adLoadComplete() {
        adLoadComplete = true;
        UserSharePref.getInstance(this).putBoolean(UserSharePref.KEY_FIRST_INSTALL, false);
        gotoMain();
    }

    /**
     * 跳转到主页面
     */
    private void gotoMain() {
        if (adLoadComplete && userInfoLoadFinish && versionLoadFinish) {
            Log.d(TAG, "goToMainActivity");
            ivSplash.setVisibility(View.VISIBLE);
            HomeActivity.goHome(SplashActivity.this);
            if (mSplashContainer != null) {
                mSplashContainer.removeAllViews();
            }
            finish();
        }
    }

    private void showToast(String msg) {
//        Log.d(TAG, msg);
        //TToast.show(this, msg);
    }

    private void doActionInThread() {
        if (!VCore.get().isEngineLaunched()) {
            VCore.get().waitForEngine();
        }
    }

}
