package com.ft.mapp.home;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.ft.mapp.ad.AdHelper;
import com.ft.mapp.ad.AdUtils;
import com.ft.mapp.ad.base.RewardAdListener;
import com.ft.mapp.ad.ttads.TTAdManagerHolder;
import com.ft.mapp.db.DBManager;
import com.ft.mapp.dialog.ClearDataDialog;
import com.ft.mapp.dialog.ShareDialog;
import com.ft.mapp.dialog.VipTipsDialog;
import com.ft.mapp.engine.GlideEngine;
import com.ft.mapp.home.activity.FaqActivityForJava;
import com.ft.mapp.home.activity.WebViewActivity;
import com.ft.mapp.home.adapters.LaunchAdapter;
import com.ft.mapp.home.models.FakeAppInfo;
import com.ft.mapp.listener.OnDialogListener;
import com.ft.mapp.utils.ActFillUtils;
import com.ft.mapp.utils.CommonUtil;
import com.ft.mapp.utils.FakeAppUtils;
import com.ft.mapp.utils.SizeUtils;
import com.ft.mapp.utils.ToastUtil;
import com.ft.mapp.utils.UIUtils;
import com.ft.mapp.utils.VipFunctionUtils;
import com.ft.mapp.widgets.DragImageView;
import com.ft.mapp.widgets.MarqueeTextView;
import com.ft.mapp.widgets.MyRecyclerView;
import com.ft.mapp.widgets.PopupFakeApp;
import com.ft.mapp.widgets.PopupFunMenu;
import com.ft.mapp.widgets.luckly_popupwindow.utils.ScreenUtils;
import com.fun.vbox.client.core.VCore;
import com.fun.vbox.helper.compat.PermissionCompat;
import com.fun.vbox.remote.InstalledAppInfo;
import com.ft.mapp.R;
import com.ft.mapp.VApp;
import com.ft.mapp.VCommends;
import com.ft.mapp.abs.nestedadapter.SmartRecyclerAdapter;
import com.ft.mapp.dialog.ShortcutDialog;
import com.ft.mapp.home.adapters.LaunchpadAdapter;
import com.ft.mapp.home.models.AppData;
import com.ft.mapp.home.models.AppInfoLite;
import com.ft.mapp.home.models.MultiplePackageAppData;
import com.ft.mapp.home.models.PackageAppData;
import com.ft.mapp.widgets.CommonDialog;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.SPUtils;
import com.viewpagerindicator.CirclePageIndicator;
import com.xqb.user.bean.VersionBean;
import com.xqb.user.net.engine.AdAgent;
import com.xqb.user.net.engine.StatAgent;
import com.xqb.user.net.engine.UserAgent;
import com.xqb.user.util.UmengStat;
import com.xqb.user.util.UserSharePref;

import java.util.ArrayList;
import java.util.List;

import jonathanfinerty.once.Once;
import zhy.com.highlight.HighLight;
import zhy.com.highlight.shape.CircleLightShape;
import zhy.com.highlight.shape.RectLightShape;

import static android.app.Activity.RESULT_OK;

public class LaunchFragment extends Fragment implements HomeContract.HomeView {
    private HomeContract.HomePresenter mPresenter;
    private List<RecyclerView> mLauncherViews = new ArrayList<>();
    private List<LaunchpadAdapter> mLaunchpadAdapters = new ArrayList<>();
    private ViewPager mViewPager;
    private CirclePageIndicator indicator;
    private ConstraintLayout layoutEmpty;
    private RelativeLayout layoutTips;
    private MarqueeTextView tvTips;
    private ImageView ivMenuLabel;
    private Handler mUiHandler;
    private int mLoadTimes;

    private LaunchAdapter pagerAdapter;
    private int currentPage = 0;
    private int maxCountInPage = 0;
    static List<AppData> appList;
    private TTAdNative mTTAdNative;
    private TTNativeExpressAd mTTAd;

    private PopupFakeApp popupFakeApp;
    private ProgressDialog progressDialog;

    private FrameLayout layoutBanner;
    private ImageView ivActBanner;
    private ImageView ivActTop;

    private TextView tvCloseAd;

    private DragImageView ivFaq;
    //    private TTRewardVideoAd currentAd;
    private boolean mReward;
//    private TTAdNative.RewardVideoAdListener rewardVideoAdListener;

    public static LaunchFragment newInstance() {
        return new LaunchFragment();
    }

    public static int currentAppCount() {
        return appList.size();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void firstLoad(List<AppInfoLite> defaultApps) {
        for (AppInfoLite defaultApp : defaultApps) {
            mPresenter.addApp(defaultApp);
        }
    }

    @Override
    public void agreePolicy() {
//        checkFirstLoad();
        loadPermission();
    }

    private void loadPermission() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE};
        if (!checkPermission(permissions)) {
            PermissionCompat.startRequestPermissions(getContext(), false, permissions, new PermissionCompat.a() {
                @Override
                public boolean a(int i, String[] strings, int[] grantResults) {
                    return PermissionCompat.isRequestGranted(grantResults);
                }
            });
        }

    }

    private boolean checkPermission(String[] permissions) {
        for (String permission : permissions) {
            int per = getContext().checkPermission(permission, Process.myPid(), Process.myUid());
            if (PackageManager.PERMISSION_GRANTED != per) {
                return false;
            }
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_launch, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUiHandler = new Handler(Looper.getMainLooper());
        bindViews(view);
        mTTAdNative = TTAdManagerHolder.get().createAdNative(requireActivity());
        new HomePresenterImpl(this).start();
        if (UserAgent.getInstance(requireContext()).isBannerOn()) {
            if (AdAgent.actHomeBannerOn()) {
                ivActBanner.setVisibility(View.VISIBLE);
                VersionBean.AdActivity adActivity = AdAgent.loadHomeBannerAct();
                ActFillUtils.fillAd(ivActBanner, adActivity);
            } else {
                ivActBanner.setVisibility(View.GONE);
                loadBannerAd();
            }
        }
        if (AdAgent.actHomeTopOn()) {
            ActFillUtils.fillAd(ivActTop, AdAgent.loadHomeTopAct());
        }
//        loadRewardAd();
    }

    //    private String bannerId = "945292746";

    private void loadBannerAd() {
        if (UserSharePref.getInstance(VApp.getApp()).getBoolean(UserSharePref.KEY_FIRST_USE, true)) {
            UserSharePref.getInstance(VApp.getApp()).putBoolean(UserSharePref.KEY_FIRST_USE, false);
            return;
        }
        float screenWidthDp = UIUtils.getScreenWidthDp(requireContext());
        float height = screenWidthDp * 120 / 344;
        //设置广告参数
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(AdUtils.getBannerId()) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(screenWidthDp, height) //期望个性化模板广告view的size,单位dp
                .setImageAcceptedSize(640, 260)//这个参数设置即可，不影响个性化模板广告的size
                .build();

        //加载广告
        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.d("---loadBannerAd---", "error");
//                layoutBanner.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        layoutBanner.removeAllViews();
//                    }
//                });
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                Log.d("---loadBannerAd---", "load banner complete = " + Thread.currentThread().getName());
                if (ads == null || ads.size() == 0) {
                    return;
                }
                mTTAd = ads.get(0);
                mTTAd.setSlideIntervalTime(10 * 1000);//设置轮播间隔 ms,不调用则不进行轮播展示
                bindAdListener(mTTAd);
                bindDislikeAction(tvCloseAd);
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
                Log.i("banner-", "onAdClicked");
            }

            @Override
            public void onAdShow(View view, int type) {
//                TToast.show(mContext, "广告展示");
                Log.i("banner-", "onAdShow");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
//                Log.e("ExpressView","render fail:"+(System.currentTimeMillis() - startTime));
//                TToast.show(mContext, msg+" code:"+code);
                Log.i("banner-", "onRenderFail");
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                //返回view的宽高 单位 dp
//                TToast.show(mContext, "渲染成功");
                Log.i("---loadBannerAd---", "banner onRenderSuccess");
                //在渲染成功回调时展示广告，提升体验
                layoutBanner.post(new Runnable() {
                    @Override
                    public void run() {
                        layoutBanner.removeAllViews();
                        layoutBanner.addView(view);
                        tvCloseAd.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private void bindDislikeAction(View dislike) {
        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutBanner.removeAllViews();
                tvCloseAd.setVisibility(View.GONE);
            }
        });
    }

    private void bindViews(View view) {
        ViewGroup layoutAnchor = view.findViewById(R.id.launch_layout_anchor);
        mViewPager = view.findViewById(R.id.home_launcher_viewpager);
        indicator = view.findViewById(R.id.home_launcher_indicator);
        layoutTips = view.findViewById(R.id.launch_layout_tips);
        tvTips = view.findViewById(R.id.launch_tv_tips);
        ivFaq = view.findViewById(R.id.launch_iv_faq);
        layoutEmpty = view.findViewById(R.id.launch_layout_empty);
        ImageView ivShare = view.findViewById(R.id.home_share_iv);
        ivMenuLabel = view.findViewById(R.id.launch_iv_menu_label);
        layoutBanner = view.findViewById(R.id.launch_layout_banner);
        ivActBanner = view.findViewById(R.id.launch_iv_act_banner);
        ivActTop = view.findViewById(R.id.home_act_iv);
        tvCloseAd = view.findViewById(R.id.launch_tv_close_ad);
        view.findViewById(R.id.launch_iv_close_tips).setOnClickListener(view1 -> layoutTips.setVisibility(View.GONE));
        View viewTop = view.findViewById(R.id.launch_view_top);
        ivShare.setOnClickListener(view1 -> new ShareDialog(getContext(), "home").show());
        ivFaq.setOnClickListener(v -> startActivity(new Intent(requireActivity(), FaqActivityForJava.class)));
        float screenHeight = UIUtils.getScreenHeight(requireContext());
        layoutAnchor.getViewTreeObserver().addOnGlobalLayoutListener(() -> ivFaq.setMaxVertical(viewTop.getMeasuredHeight() + SizeUtils.dip2px(30), screenHeight - SizeUtils.dip2px(80) - ivFaq.getMeasuredHeight()));
        if (UserAgent.getInstance(requireActivity()).isVirtualLocationOn()) {
            ivFaq.setVisibility(View.VISIBLE);
        } else {
            ivFaq.setVisibility(View.GONE);
        }
    }

    private void initLaunchpad() {
        pagerAdapter = new LaunchAdapter();
        pagerAdapter.setList(mLauncherViews);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        updatePager();
        indicator.setViewPager(mViewPager);
    }

    private void updatePager() {
        mLauncherViews.clear();
        mLaunchpadAdapters.clear();
        //计算viewpager一共显示几页
        int size = appList.size();
        int pageCount;
        if (size <= maxCountInPage) {
            pageCount = 1;
        } else {
            pageCount = size % maxCountInPage == 0
                    ? size / maxCountInPage
                    : size / maxCountInPage + 1;
        }
        for (int i = 0; i < pageCount; i++) {
            List<AppData> pageAppInfos;
            int endIndex;
            //首页展示上限
            endIndex = maxCountInPage * (i + 1);
            if (endIndex >= size) {
                endIndex = size;
            }
            pageAppInfos = appList.subList(maxCountInPage * (i), endIndex);
            ArrayList<AppData> appData = new ArrayList<>(pageAppInfos);
            mLauncherViews.add(i, generateListView(appData, i));
        }
        pagerAdapter = new LaunchAdapter();
        pagerAdapter.setList(mLauncherViews);
        mViewPager.setAdapter(pagerAdapter);
        if (mLauncherViews.size() < 2) {
            indicator.setVisibility(View.INVISIBLE);
        } else {
            indicator.setVisibility(View.VISIBLE);
        }
        indicator.invalidate();
        if (size == 0) {
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
        }

    }

    private RecyclerView generateListView(List<AppData> appInfos, int index) {
        Context context = getContext();
        MyRecyclerView mLauncherView = (MyRecyclerView) LayoutInflater.from(getContext()).inflate(R.layout.layout_launcher_app_recyclerview, mViewPager, false);
        mLauncherView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 3);
        mLauncherView.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) mLauncherView.getItemAnimator()).setSupportsChangeAnimations(false);
        LaunchpadAdapter mLaunchpadAdapter;
        mLaunchpadAdapter = new LaunchpadAdapter(context);
        mLaunchpadAdapter.setList(appInfos);
        SmartRecyclerAdapter wrap = new SmartRecyclerAdapter(mLaunchpadAdapter);
        //首页添加广告布局
//        if (index == 0) {
//            View header = View.inflate(requireContext(), R.layout.layout_launch_header, null);
////            UIUtils.getScreenWidth(requireContext())
//            header.setLayoutParams(new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    (int) (UIUtils.getScreenWidth(requireContext()) * 120f / 334f)));
//            header.findViewById(R.id.launch_header_iv_ad).setOnClickListener(v -> {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                Uri content_url = Uri.parse("http://app.fntmob.com/qsy");
//                intent.setData(content_url);
//                startActivity(intent);
//            });
//            wrap.setHeaderView(header);
//        }

        mLauncherView.setAdapter(wrap);
//        mLauncherView.addItemDecoration(new ItemOffsetDecoration(context, R.dimen.desktop_divider));
        mLaunchpadAdapter.setAppClickListener((view, targetView, pos, data) -> {
            if (data == null) {
                ListAppActivity.gotoListApp(getActivity());
            } else {
                gotoAppDetail(data);
            }
        });

        PopupFunMenu funMenu = new PopupFunMenu(context);
//        funMenu.showMultiMenu(false);
        mLaunchpadAdapter.setMoreClickListener((view, targetView, pos, data) -> {
            InstalledAppInfo installedAppInfo = VCore.get().getInstalledAppInfo(data.getPackageName(), 0);
            if (installedAppInfo != null) {
                funMenu.showMultiMenu(true);
            } else {
                funMenu.showMultiMenu(false);
            }
            funMenu.setOnDismissListener(() -> {
            });
            funMenu.setOnItemClickListener(item -> {
                switch (item) {
                    case FAKE:
//                        if (checkVip()) {
//                            showFakePopup(mLauncherView, pos, data);
//                        }
                        showFakePopup(mLauncherView, pos, data);
                        break;
                    case SETTING:
                        gotoAppDetail(data);
                        break;
                    case SHORTCUT:
                        int userId = 0;
                        if (data instanceof MultiplePackageAppData) {
                            userId = ((MultiplePackageAppData) data).userId;
                        }
                        new ShortcutDialog(getContext(), userId, data.getPackageName(), data).show();
                        break;
                    case DELETE:
                        deleteApp(pos);
                        break;
                    case MULTI:
                        checkAddLimit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    PackageInfo pkg = VApp.getApp().getPackageManager()
                                            .getPackageInfo(data.getPackageName(), 0);
                                    ApplicationInfo ai = pkg.applicationInfo;
                                    String path =
                                            ai.publicSourceDir != null ? ai.publicSourceDir : ai.sourceDir;
                                    if (!TextUtils.isEmpty(path)) {
                                        AppInfoLite lite = new AppInfoLite(data.getPackageName(), path,
                                                true);
                                        mPresenter.addApp(lite);
                                    }
                                } catch (Throwable ignore) {
                                }
                            }
                        });
                        break;
                    case CLEAR:
                        showClearData(index, data);
                        break;
                }
            });

//            funMenu.showLocation(view, ScreenUtils.getScreenWidth(requireContext()), (int) UIUtils.getHeight(requireActivity()));
//            funMenu.setWidth(ScreenUtils.dp2px(requireContext(),160));
            funMenu.showLocation(requireActivity().getWindow().getDecorView(), view, targetView);
            AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
            animation.setDuration(150);
            ivMenuLabel.setVisibility(View.VISIBLE);
            int[] location = new int[2];
            targetView.getLocationInWindow(location);
            ivMenuLabel.setX(location[0] + SizeUtils.dip2px(8));
            ivMenuLabel.setY(location[1]);
            ivMenuLabel.startAnimation(animation);
        });

        mLaunchpadAdapters.add(index, mLaunchpadAdapter);
        return mLauncherView;
    }

    private Runnable limitRunnable;

    private void checkAddLimit(Runnable runnable) {
        if (UserAgent.getInstance(requireContext()).isVipUser() || appList.size() < 4) {
            runnable.run();
            return;
        }
        //非会员
        limitRunnable = runnable;
        VipTipsDialog vipTipsDialog = new VipTipsDialog(requireActivity(), VipFunctionUtils.FUNCTION_ADD_LIMIT);
        vipTipsDialog.setOnVipAdListener(new VipTipsDialog.OnVipAdListener() {
            @Override
            public void adListener() {
                showAd();
            }
        });
        vipTipsDialog.show();
    }

    private void showAd() {
//        if (currentAd != null) {
//            currentAd.showRewardVideoAd(requireActivity());
//            currentAd = null;
//        } else {
//            ToastUtil.show(requireActivity(), "广告还没准备好，请稍候再试");
//            loadRewardAd();
//        }
        new AdHelper(requireActivity()).showVipRewardAd(new RewardAdListener() {
            @Override
            public void onError(int code, String msg) {
                ToastUtil.show(requireActivity(), "广告还没准备好，请稍候再试");
            }

            @Override
            public void onTimeout() {

            }

            @Override
            public void onAdClicked() {

            }

            @Override
            public void onAdShow() {

            }

            @Override
            public void onRewardVerify() {
                mReward = true;
            }

            @Override
            public void onAdSkip() {

            }

            @Override
            public void onAdClose() {

            }
        });
    }
//
//    private void loadRewardAd() {
//        if (!UserAgent.getInstance(requireActivity()).isRewardOn()) {
//            return;
//        }
//        AdSlot adSlot = new AdSlot.Builder()
//                .setCodeId(AdUtils.getVipRewardId())
//                .setSupportDeepLink(true)
//                .setOrientation(TTAdConstant.VERTICAL)
//                .build();
//        rewardVideoAdListener = new TTAdNative.RewardVideoAdListener() {
//            @Override
//            public void onError(int i, String s) {
//
//            }
//
//            @Override
//            public void onRewardVideoAdLoad(TTRewardVideoAd ttRewardVideoAd) {
//                currentAd = ttRewardVideoAd;
//                currentAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
//                    @Override
//                    public void onAdShow() {
//
//                    }
//
//                    @Override
//                    public void onAdVideoBarClick() {
//
//                    }
//
//                    @Override
//                    public void onAdClose() {
//
//                    }
//
//                    @Override
//                    public void onVideoComplete() {
//                        mTTAdNative.loadRewardVideoAd(adSlot, rewardVideoAdListener);
//                    }
//
//                    @Override
//                    public void onVideoError() {
//
//                    }
//
//                    @Override
//                    public void onRewardVerify(boolean b, int i, String s, int i1, String s1) {
//                        mReward = b;
//                    }
//
//                    @Override
//                    public void onSkippedVideo() {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onRewardVideoCached() {
//
//            }
//        };
//        mTTAdNative.loadRewardVideoAd(adSlot, rewardVideoAdListener);
//    }

    private void showClearData(int pos, AppData data) {
        ClearDataDialog dataDialog = new ClearDataDialog(requireContext());
        dataDialog.setOnDialogListener(new OnDialogListener() {
            @Override
            public void onCancel() {
            }

            @Override
            public void onOk() {
                showLoading();
                if (data.getFakeAppInfo() != null) {
                    DBManager.INSTANCE.getDaoSession().getFakeAppInfoDao().deleteByKey(data.getFakeAppInfo().getId());
                }
                if (data instanceof MultiplePackageAppData) {
                    ((MultiplePackageAppData) data).fakeAppInfo = null;
                } else if (data instanceof PackageAppData) {
                    ((PackageAppData) data).fakeAppInfo = null;
                }
//                LaunchpadAdapter launchpadAdapter = mLaunchpadAdapters.get(currentPage);
//                launchpadAdapter.notifyItemChanged(pos);
//                launchpadAdapter.notifyItemRangeChanged(pos,launchpadAdapter.getItemCount());
                VCore.get().cleanPackageData(data.getPackageName(), data.getUserId());
                updatePager();
                mViewPager.setCurrentItem(currentPage);
                mUiHandler.postDelayed(() -> hideLoading(), 1000);
                ToastUtil.show(requireContext(), "修复完成");
            }
        });
        dataDialog.show();
    }

    private void showFakePopup(View parent, int index, AppData appData) {
        popupFakeApp = new PopupFakeApp(requireContext());
        popupFakeApp.showLocation(parent, appData);
        popupFakeApp.setOnDismissListener(() -> setBackgroundAlpha(1f));
        popupFakeApp.setFakeAppOperateListener(new PopupFakeApp.FakeAppOperateListener() {
            @Override
            public void onSubmit(String fakeTitle, String fakeIcon, int appId) {
                FakeAppInfo fakeAppInfo = appData.getFakeAppInfo();
                if (fakeAppInfo == null) {
                    fakeAppInfo = new FakeAppInfo();
                } else {
                    fakeAppInfo.setId(fakeAppInfo.getId());
                }
                fakeAppInfo.setAppId(appId);
                if (!TextUtils.isEmpty(fakeTitle)) {
                    fakeAppInfo.setFakeName(fakeTitle);
                }

                if (!TextUtils.isEmpty(fakeIcon)) {
                    fakeAppInfo.setFakeIcon(fakeIcon);
                }
                if (appData instanceof PackageAppData) {
                    ((PackageAppData) appData).fakeAppInfo = fakeAppInfo;
                } else if (appData instanceof MultiplePackageAppData) {
                    ((MultiplePackageAppData) appData).fakeAppInfo = fakeAppInfo;
                }
                StatAgent.onEvent(requireContext(), UmengStat.FAKE_APP, "name",
                        appData.getName());
                FakeAppUtils.insert(fakeAppInfo);
                LaunchpadAdapter launchpadAdapter = mLaunchpadAdapters.get(currentPage);
//                if (currentPage == 0 && index == 0) {
//                    updatePager();
////                    launchpadAdapter.notifyDataSetChanged();
//                } else {
//                launchpadAdapter.notifyDataSetChanged();
                launchpadAdapter.notifyItemChanged(index);
                launchpadAdapter.notifyItemRangeChanged(index, launchpadAdapter.getItemCount());
//                }
//                updatePager();

            }

            @Override
            public void choosePic() {
                chooseFakePic();
            }
        });
        setBackgroundAlpha(0.5f);
    }

    private void chooseFakePic() {
        PictureSelector.create(this)
                .openGallery(PictureConfig.TYPE_IMAGE)
                .imageEngine(GlideEngine.createGlideEngine())
                .imageSpanCount(4)// 每行显示个数 int
//                .maxSelectNum(1)
                .withAspectRatio(1, 1)
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isSingleDirectReturn(true)//PictureConfig.SINGLE模式下是否直接返回
                .isPreviewImage(true)// 是否可预览图片 true or false
                .isCamera(true)// 是否显示拍照按钮 true or false
                .imageFormat(PictureMimeType.JPEG)// 拍照保存图片格式后缀,默认jpeg
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
//                .setOutputCameraPath(Const.getImgPath())// 自定义拍照保存路径,可不填
                .isEnableCrop(true)// 是否裁剪 true or false
                .isCompress(true)// 是否压缩 true or false
//                .compressSavePath(Const.getImgPath())//压缩图片保存地址
                .freeStyleCropEnabled(false)// 裁剪框是否可拖拽 true or false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    public void addApp() {
        ListAppActivity.gotoListApp(getActivity());
    }

    private void gotoAppDetail(AppData data) {
        try {
            String targetName = CommonUtil.getAppFakeName(data);
            Drawable targetIcon = CommonUtil.getAppFakeIcon(data);

            if (data instanceof PackageAppData) {
                PackageAppData appData = (PackageAppData) data;
                AppDetailActivity.gotoAppDetail(getActivity(), appData);
            } else if (data instanceof MultiplePackageAppData) {
                MultiplePackageAppData multipleData = (MultiplePackageAppData) data;
//                if (checkVip()) {
//                    AppDetailActivity.gotoAppDetail(getActivity(), targetName, multipleData.appInfo.packageName,
//                            multipleData.userId, targetIcon);
//                }
                if (!UserAgent.getInstance(requireActivity()).isVipUser()) {
                    new VipTipsDialog(requireActivity(), VipFunctionUtils.DEFAULT).show();
                } else {
                    AppDetailActivity.gotoAppDetail(getActivity(), multipleData);
                }
            } else {
                AppDetailActivity.gotoAppDetail(getActivity(), data);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

//    private boolean checkVip() {
//        if (!UserAgent.getInstance(getActivity()).isVipUser()) {
//            VipTipsDialog vipTipsDialog = new VipTipsDialog(getActivity());
//            vipTipsDialog.show();
//            return false;
//        }
//        return true;
//    }

    private void deleteApp(int position) {
        AppData data = mLaunchpadAdapters.get(currentPage).getList().get(position);
        String name = data.getName();
        if (data instanceof MultiplePackageAppData) {
            int nameIndex = ((MultiplePackageAppData) data).userId + 1;
            name = name + "(" + nameIndex + ")";
        }
        new CommonDialog(getContext())
                .setTitleId(R.string.delete_app)
                .setMessage(getString(R.string.delete_app_msg, name))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> mPresenter.deleteApp(data))
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    public void setPresenter(HomeContract.HomePresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoading() {
//        mLoadingView.setVisibility(View.VISIBLE);
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(requireContext());
            progressDialog.setTitle("请稍候");
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
//        mLoadingView.setVisibility(View.GONE);
    }


    private int maxHeight = 0;

    @Override
    public void loadFinish(List<AppData> list) {
//        list.add(new AddAppData());
        mViewPager.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (maxHeight != mViewPager.getMeasuredHeight()) {
                    maxHeight = mViewPager.getMeasuredHeight();
                    mViewPager.getViewTreeObserver().removeOnPreDrawListener(this);
                    calculateSize();
                    appList = list;
//        mLaunchpadAdapters.get(currentPage).setList(list);
                    initLaunchpad();
                    hideLoading();
                    mPresenter.updateUserInfo(false);
                    mPresenter.updateProductInfo();
                }
                return true;
            }
        });

    }

    private void calculateSize() {
//        float itemHeight = bannerHeight + SizeUtils.dip2px(8);
        int screenWidth = ScreenUtils.getScreenWidth(requireContext());
        float bannerHeight = screenWidth * 120f / 344f;//广告高度
        //app item高度为114dp
        if (maxHeight == 0) {
            maxHeight = (int) ((ScreenUtils.getScreenHeight(requireContext()) * 2f / 3f));
        }
        maxCountInPage = (int) ((maxHeight - bannerHeight) / bannerHeight) * 3;
        if (maxCountInPage == 0) {
            maxCountInPage = 9;
        }

    }

    @Override
    public void loadError(Throwable err) {
        hideLoading();
        if (mLoadTimes > 3) {
            mUiHandler.postDelayed(() -> {
                mPresenter.dataChanged();
                mLoadTimes++;
            }, 300);
        }
        err.printStackTrace();
    }

    @Override
    public void showGuide() {

    }

    @Override
    public void addAppToLauncher(AppData model) {
        appList.add(model);
        if (appList.size() > 0) {
            layoutEmpty.setVisibility(View.GONE);
        }
        LaunchpadAdapter lastLaunchpadAdapter = mLaunchpadAdapters.get(mLauncherViews.size() - 1);
        List<AppData> dataList = lastLaunchpadAdapter.getList();
        if (dataList != null && dataList.size() == maxCountInPage) {
            updatePager();
        } else {
            lastLaunchpadAdapter.add(model);
        }

        mViewPager.setCurrentItem(mLauncherViews.size() - 1);

        if (UserSharePref.getInstance(VApp.getApp()).getBoolean(UserSharePref.KEY_GUIDE_FUNCTION)) {
            return;
        }
        RecyclerView recyclerView = mLauncherViews.get(mLauncherViews.size() - 1);
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                checkFirstView();
                recyclerView.removeOnChildAttachStateChangeListener(this);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {

            }
        });
    }

    private void checkFirstView() {
        View firstView = mLaunchpadAdapters.get(currentPage).getFirstView();
        if (firstView != null) {
            firstView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    synchronized (LaunchFragment.this) {
                        if (!readyFirstView) {
                            readyFirstView = true;
                            firstView.getViewTreeObserver().removeOnPreDrawListener(this);
                            LaunchFragment.this.showFirstAddAppTips();
                        }
                    }
                    return true;
                }
            });
        }
    }

    private volatile Boolean readyFirstView = false;
    private HighLight highLight;

    private void showFirstAddAppTips() {
        if (!readyFirstView) {
            return;
        }

        highLight = new HighLight(requireActivity())
                .autoRemove(false)
                .enableNext()
                .setOnLayoutCallback(() -> {
//                        layoutCall = true;
                })
                .setClickCallback(() -> {
                    try {
                        highLight.next();
                    } catch (Exception ignore) {

                    }
                })
                .setOnRemoveCallback(() -> {
//                        onFinishTipsListener.onFinishTips();
                    UserSharePref.getInstance(VApp.getApp()).putBoolean(UserSharePref.KEY_GUIDE_FUNCTION, true);
                });
        UserSharePref.getInstance(VApp.getApp()).putBoolean(UserSharePref.KEY_GUIDE_FUNCTION, true);
        if (highLight.isShowing()) {
            return;
        }
        showFirstAddApp();
    }

    private void showFirstAddApp() {
        //暂定延时1s，adapter渲染延迟
        mUiHandler.post(() -> {
            View firstView = mLaunchpadAdapters.get(currentPage).getFirstView();
            highLight.addHighLight(firstView, R.layout.layout_tips_first_add, (rightMargin, bottomMargin, rectF, marginInfo) -> {
                marginInfo.leftMargin = 0;
                marginInfo.rightMargin = 0;
                marginInfo.topMargin = rectF.bottom;
            }, new RectLightShape(10, 5));

            highLight.addHighLight(firstView.findViewById(R.id.item_app_more_iv), R.layout.layout_tips_more_fun, (rightMargin, bottomMargin, rectF, marginInfo) -> {
                marginInfo.leftMargin = rectF.width();
                marginInfo.rightMargin = 0;
                marginInfo.topMargin = rectF.bottom;
            }, new CircleLightShape());

            highLight.show();

        });

    }
//
//    OnFinishTipsListener onFinishTipsListener;
//
//    public void setOnFinishTipsListener(OnFinishTipsListener onFinishTipsListener) {
//        this.onFinishTipsListener = onFinishTipsListener;
//    }
//
//    public interface OnFinishTipsListener {
//        void onFinishTips();
//    }

    @Override
    public void removeAppToLauncher(AppData model) {
        appList.remove(model);
        if (model.getFakeAppInfo() != null) {
            DBManager.INSTANCE.getDaoSession().getFakeAppInfoDao().deleteByKey(model.getFakeAppInfo().getId());
        }
        updatePager();
        mViewPager.setCurrentItem(Math.min(currentPage, mLauncherViews.size() - 1));
//        if (currentPage != mLauncherViews.size() - 1) {
//
//        } else {
//            mLaunchpadAdapters.get(currentPage).remove(model);
//        }
    }

    @Override
    public void refreshLauncherItem(AppData model) {
        mLaunchpadAdapters.get(currentPage).refresh(model);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == AppDetailActivity.REQUEST_CODE_DETAIL) {
                mPresenter.dataChanged();
            } else if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList != null && selectList.size() > 0) {
                    popupFakeApp.setCurIv(selectList.get(0).getCutPath());
                }
            } else {
                List<AppInfoLite> appList = data.getParcelableArrayListExtra(VCommends.EXTRA_APP_INFO_LIST);
                if (appList != null) {
                    for (AppInfoLite info : appList) {
                        mPresenter.addApp(info);
                    }
                }
            }
        }
    }

    private void setBackgroundAlpha(float alpha) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.alpha = alpha;
            activity.getWindow().setAttributes(lp);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tvTips != null) {
            tvTips.requestFocus();
        }
        if (mReward && limitRunnable != null) {
            limitRunnable.run();
            mReward = false;
            VipFunctionUtils.markFunction(VipFunctionUtils.FUNCTION_ADD_LIMIT);
        }
    }

}
