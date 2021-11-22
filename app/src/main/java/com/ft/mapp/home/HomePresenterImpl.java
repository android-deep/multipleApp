package com.ft.mapp.home;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ft.mapp.R;
import com.ft.mapp.VCommends;
import com.ft.mapp.VConstant;
import com.ft.mapp.ad.ttads.OnTTRvLisenter;
import com.ft.mapp.dialog.LiabilityDialog;
import com.ft.mapp.dialog.LoadingBottomDialog;
import com.ft.mapp.dialog.LoadingDialog;
import com.ft.mapp.home.models.AppData;
import com.ft.mapp.home.models.AppInfo;
import com.ft.mapp.home.models.AppInfoLite;
import com.ft.mapp.home.models.FakeAppInfo;
import com.ft.mapp.home.models.MultiplePackageAppData;
import com.ft.mapp.home.models.PackageAppData;
import com.ft.mapp.home.repo.AppRepository;
import com.ft.mapp.home.repo.PackageAppDataStorage;
import com.ft.mapp.open.MultiAppHelper;
import com.ft.mapp.utils.AppSharePref;
import com.ft.mapp.utils.CommonUtil;
import com.ft.mapp.utils.InstallHelper;
import com.ft.mapp.widgets.CommonDialog;
import com.fun.vbox.client.core.VCore;
import com.fun.vbox.client.ipc.VActivityManager;
import com.fun.vbox.client.ipc.VirtualLocationManager;
import com.fun.vbox.helper.compat.PermissionCompat;
import com.fun.vbox.remote.InstallResult;
import com.fun.vbox.remote.InstalledAppInfo;
import com.fun.vbox.server.bit64.Bit64Utils;
import com.xqb.user.bean.UserInfo;
import com.xqb.user.bean.VersionBean;
import com.xqb.user.bean.VipProductInfo;
import com.xqb.user.net.engine.ApiServiceDelegate;
import com.xqb.user.net.engine.StatAgent;
import com.xqb.user.net.engine.UserAgent;
import com.xqb.user.net.engine.VUiKit;
import com.xqb.user.util.UmengStat;
import com.xqb.user.util.UserSharePref;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import jonathanfinerty.once.Once;

/**
 *
 */
class HomePresenterImpl implements HomeContract.HomePresenter {
    private static final int MSG_GAME_REWARD_VIDEO_LOADING = 1001;

    private HomeContract.HomeView mView;
    private Activity mActivity;
    private AppRepository mRepo;
    private boolean mDialogShowing;
    private LoadingBottomDialog mLoadingDialog;

    AppData mAppData;
    private MHandler mHandler;

    HomePresenterImpl(HomeContract.HomeView view) {
        mView = view;
        mActivity = view.getActivity();
        mRepo = new AppRepository(mActivity);
        mView.setPresenter(this);
        mHandler = new MHandler(this);
        //mHandler.sendEmptyMessageDelayed(MSG_GAME_REWARD_VIDEO_LOADING, 3000);
    }

    @Override
    public void firstInstallRecommendApps() {
        mRepo.getInstalledRecmApps(mActivity).done(result -> {
            ArrayList<AppInfoLite> dataList = new ArrayList<>(result.size());
            for (AppInfo appInfo : result) {
                dataList.add(new AppInfoLite(appInfo.packageName, appInfo.path, appInfo.fastOpen));
            }

            mView.firstLoad(dataList);
        });
    }

    @Override
    public void start() {
        dataChanged();
        if (!Once.beenDone(VCommends.TAG_SHOW_ADD_APP_GUIDE)) {
            mView.showGuide();
            Once.markDone(VCommends.TAG_SHOW_ADD_APP_GUIDE);
        }
        showWritePermission();
    }

    private void showWritePermission() {
        if (mDialogShowing)
            return;

        if (!PermissionCompat
                .checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, false)) {
            PermissionCompat.startRequestPermissions(mView.getContext(), false,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    (requestCode, permissions, grantResults) -> {
                        boolean result = PermissionCompat.isRequestGranted(grantResults);
                        if (result) {

                        }
                        return result;
                    });
        }
    }

    private void startApp() {
        String mPkg = mAppData.getPackageName();
        String mName = mAppData.getName();
        int userId = 0;

        if (mAppData instanceof MultiplePackageAppData) {
            userId = ((MultiplePackageAppData) mAppData).userId;
        }
        if (!CommonUtil.isAppInstalled(mActivity, mPkg)) {
            CommonUtil.launchAppMarket(mPkg, "", mName);
        } else if (TextUtils.equals(VConstant.PUBG_MOBILE_PACKAGE_NAME, mPkg)
                || TextUtils.equals(VConstant.PUBG2_MOBILE_PACKAGE_NAME, mPkg)) {//PUBG
            CommonUtil.startAppByPkgName(mActivity, mPkg);
        } else {
            if (mLoadingDialog == null) {
                mLoadingDialog = new LoadingBottomDialog(mActivity, mName);
                mLoadingDialog.setCanceledOnTouchOutside(false);
            }
            mLoadingDialog.setMessage(mName);
            mLoadingDialog.show();
            initDualApp(mPkg, userId);
        }

//        if (isBit64) {
//            StatAgent.onEvent(mActivity, UmengStat.DUAL_LAUNCH_64, "name", mName);
//        } else {
//            StatAgent.onEvent(mActivity, UmengStat.DUAL_LAUNCH, "name", mName);
//        }
    }

    @Override
    public void launchApp(AppData data) {
    }

    private void rewardVideo() {

    }


    /**
     * 初始化分身
     */
    private void initDualApp(String packageName, int userId) {
        VUiKit.defer().when(() -> {
            try {
                InstallHelper.installPackage(mActivity, packageName);
            } catch (PackageManager.NameNotFoundException ignored) {
            }
            Intent mIntent = VCore.get().getLaunchIntent(packageName, userId);
            if (mIntent == null) {
                return;
            }
            VCore.get().setUiCallback(mIntent, mUiCallback);
            VActivityManager.get().startActivity(mIntent, userId);
        }).done(result -> {
        });
    }

    private final VCore.UiCallback mUiCallback = new VCore.UiCallback() {

        @Override
        public void onAppOpened(String packageName, int userId) {
            AndroidSchedulers.mainThread().scheduleDirect(() -> {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            });
        }
    };

    @Override
    public void dataChanged() {
        mView.showLoading();
        mRepo.getVirtualApps().done(mView::loadFinish).fail(mView::loadError);
    }


    @Override
    public void addApp(AppInfoLite info) {
        class AddResult {
            private PackageAppData appData;
            private int userId;
        }
        AddResult addResult = new AddResult();
        Dialog dialog = new LoadingDialog(mActivity,mActivity.getString(R.string.first_add_tip));
        dialog.show();
        VUiKit.defer().when(() -> {
            InstalledAppInfo installedAppInfo = VCore.get().getInstalledAppInfo(info.packageName, 0);
            if (installedAppInfo != null) {
                addResult.userId = MultiAppHelper.installExistedPackage(installedAppInfo);
            } else {
                InstallResult res = mRepo.addVirtualApp(info);
                if (!res.isSuccess) {
                    throw new IllegalStateException();
                }
            }
        }).then((res) ->
                addResult.appData = PackageAppDataStorage.get().acquire(info.packageName)).fail((e) -> {
            new CommonDialog(mActivity)
                    .setTitleId(R.string.notice)
                    .setMessage(R.string.tip_64)
                    .setPositiveButton(R.string.OK, (dialogInterface, i) -> {
                        if (Bit64Utils.isRunOn64BitProcess(info.packageName) && !VCore.get().is64BitEngineInstalled()) {
                            CommonUtil.install64Bit();
                        }
                    }).show();
            dialog.dismiss();
        }).done(res -> {
            dialog.dismiss();
            if (addResult.userId == 0) {
                PackageAppData data = addResult.appData;
                data.isLoading = true;
                mView.addAppToLauncher(data);
                handleOptApp(data, info.packageName, true);
            } else {
                MultiplePackageAppData data = new MultiplePackageAppData(addResult.appData, addResult.userId);
                data.isLoading = true;
                mView.addAppToLauncher(data);
                handleOptApp(data, info.packageName, false);
            }
        });
    }


    private void handleOptApp(AppData data, String packageName, boolean needOpt) {
        VUiKit.defer().when(() -> {
            long time = System.currentTimeMillis();
            if (needOpt) {
                try {
                    VCore.get().preOpt(packageName);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            time = System.currentTimeMillis() - time;
            if (time < 1500L) {
                try {
                    Thread.sleep(1500L - time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).done((res) -> {
            if (data instanceof PackageAppData) {
                ((PackageAppData) data).isLoading = false;
                ((PackageAppData) data).isFirstOpen = true;
            } else if (data instanceof MultiplePackageAppData) {
                ((MultiplePackageAppData) data).isLoading = false;
                ((MultiplePackageAppData) data).isFirstOpen = true;
            }
            mView.refreshLauncherItem(data);
            StatAgent.onEvent(mView.getContext(), UmengStat.ADD_APP, "name", data.getName());
        });
    }

    @Override
    public void deleteApp(AppData data) {
        try {
            mView.removeAppToLauncher(data);
            if (data instanceof PackageAppData) {
                mRepo.removeVirtualApp(((PackageAppData) data).packageName, 0);
            } else {
                MultiplePackageAppData appData = (MultiplePackageAppData) data;
                mRepo.removeVirtualApp(appData.appInfo.packageName, appData.userId);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createShortcut(AppData data) {
        VCore.OnEmitShortcutListener listener = new VCore.OnEmitShortcutListener() {
            @Override
            public Bitmap getIcon(Bitmap originIcon) {
                BitmapDrawable appFakeIcon = (BitmapDrawable) CommonUtil.getAppFakeIcon(data);
                return appFakeIcon.getBitmap();
//                return originIcon;
            }

            @Override
            public String getName(String originName) {
                String name = CommonUtil.getAppFakeName(data);
                return name + "(分身)";
            }
        };
        if (data instanceof PackageAppData) {
            VCore.get().createShortcut(0, ((PackageAppData) data).packageName, listener);
        } else if (data instanceof MultiplePackageAppData) {
            MultiplePackageAppData appData = (MultiplePackageAppData) data;
            VCore.get().createShortcut(appData.userId, appData.appInfo.packageName, listener);
        }
    }

    @Override
    public void checkApkUpdate() {
    }

    @Override
    public void updateUserInfo(boolean forceUpdate) {
        if (forceUpdate) {
            new ApiServiceDelegate(mActivity).getUserInfo();
            return;
        }
        UserInfo userInfo = UserAgent.getInstance(mActivity).getUserInfo();

        if (userInfo == null) {
            return;
        }
        if ((System.currentTimeMillis() - userInfo.responseTime > 10800000L)
                || UserSharePref.getInstance(mActivity).getBoolean(UserSharePref.KEY_PENDING_USER_INFO)) {
            new ApiServiceDelegate(mActivity).getUserInfo();
        }
    }

    @Override
    public void updateProductInfo() {
        VipProductInfo productInfo = UserAgent.getInstance(mActivity).getProductInfo();
        if (productInfo == null
                || (System.currentTimeMillis() - productInfo.responseTime > productInfo.interval * 1000L
                || productInfo.interval > 86400L)) {
            new ApiServiceDelegate(mActivity).getProduct();
        }
    }

    @Override
    public void dismissLoading() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    private void clearLocation(String mPkg, int mUserId) {
        try {
            VirtualLocationManager.get().setMode(mUserId, mPkg, VirtualLocationManager.MODE_CLOSE);
        } catch (Exception e) {
        }
    }


    private static class MHandler extends Handler {
        private WeakReference<HomePresenterImpl> test;

        public MHandler(HomePresenterImpl activity) {
            test = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (test != null) {
                switch (msg.what) {
                    case MSG_GAME_REWARD_VIDEO_LOADING:
                        //test.loadRewardAd();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
