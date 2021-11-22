package com.ft.mapp.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.ft.mapp.dialog.VipTipsDialog;
import com.ft.mapp.home.activity.VipActivity;
import com.ft.mapp.home.location.ChooseBDLocationActivity;
import com.ft.mapp.home.models.AppData;
import com.ft.mapp.home.models.FakeAppInfo;
import com.ft.mapp.home.models.LocationData;
import com.ft.mapp.home.models.MultiplePackageAppData;
import com.ft.mapp.home.models.PackageAppData;
import com.ft.mapp.utils.FakeAppUtils;
import com.xqb.user.net.engine.StatAgent;
import com.ft.mapp.R;
import com.ft.mapp.VCommends;
import com.ft.mapp.VConstant;
import com.ft.mapp.abs.ui.VActivity;
import com.ft.mapp.dialog.LoadingBottomDialog;
import com.ft.mapp.dialog.ShortcutDialog;
import com.ft.mapp.engine.GlobalData;
import com.ft.mapp.home.activity.PluginStepActivity;
import com.ft.mapp.home.activity.PluginTiktokActivity;
import com.ft.mapp.home.adapters.AppPluginAdapter;
import com.ft.mapp.home.adapters.decorations.ItemOffsetDecoration;
import com.ft.mapp.home.device.DeviceDetailActivity;
import com.ft.mapp.home.location.ChooseBDLocationActivity;
import com.ft.mapp.home.models.DeviceData;
import com.ft.mapp.home.models.PluginInfo;
import com.ft.mapp.utils.AppPackageCompat;
import com.ft.mapp.utils.AppSharePref;
import com.ft.mapp.utils.CommonUtil;
import com.ft.mapp.utils.InstallHelper;
import com.fun.vbox.client.core.VCore;
import com.fun.vbox.client.ipc.VActivityManager;
import com.fun.vbox.client.ipc.VirtualLocationManager;
import com.fun.vbox.helper.compat.PermissionCompat;
import com.fun.vbox.os.VUserInfo;
import com.fun.vbox.os.VUserManager;
import com.fun.vbox.server.bit64.Bit64Utils;
import com.gw.swipeback.SwipeBackLayout;
import com.xqb.user.net.engine.UserAgent;
import com.xqb.user.net.engine.VUiKit;
import com.xqb.user.util.UmengStat;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class AppDetailActivity extends VActivity {
    private static final String TAG = "AppDetailActivity";
    private static final String PKG_NAME_ARGUMENT = "MODEL_ARGUMENT";
    private static final String NAME_ARGUMENT = "NAME_ARGUMENT";
    private static final String KEY_USER = "KEY_USER";
    private static final String KEY_APP_ID = "KEY_APP_ID";
    private static final String APP_ICON = "app_icon";
    private static final String FAKE_INFO = "fake_info";

    public static final int REQUEST_CODE_DETAIL = 0x1212;

    private final int MSG_WAIT_INSTALL = 0x10;

    private AppPluginAdapter mAdapter;
    private String mPkg;
    private String mName;
    private int mUserId;
    private int mAppId;
    private LoadingBottomDialog mLoadingDialog;
    private volatile Intent mIntent;
    private volatile boolean mInstalling;
    private long mLaunchTimestamp;
    private PluginInfo mClickInfo;

    private LocationData simLocationData;
    private boolean simLocationOpen;

    private FakeAppInfo fakeAppInfo;

    public static void gotoAppDetail(Context context, AppData appData) {
        gotoAppDetail(context, FakeAppUtils.fakeAppMap.get(Integer.parseInt(appData.getAppId() + "" + appData.getUserId())), CommonUtil.getAppFakeName(appData), appData.getPackageName(), appData.getAppId(), appData.getUserId(), CommonUtil.getAppFakeIcon(appData));
    }

    public static void gotoAppDetail(Context context, String name, String packageName, int userId,
                                     Drawable drawable) {
        gotoAppDetail(context, null, name, packageName, -1, userId, drawable);
    }

    public static void gotoAppDetail(Context context, FakeAppInfo fakeAppInfo, String name, String packageName, int appId, int userId,
                                     Drawable drawable) {
        String packageCompat = AppPackageCompat.getPackageName(packageName);

        Intent intent = new Intent(context, AppDetailActivity.class);
        intent.putExtra(FAKE_INFO, fakeAppInfo);
        intent.putExtra(PKG_NAME_ARGUMENT, packageCompat);
        intent.putExtra(NAME_ARGUMENT, name);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_USER, userId);
        intent.putExtra(KEY_APP_ID, appId);
        if (drawable != null) {
            byte[] bytes = CommonUtil.drawable2Bytes(drawable);
            intent.putExtra(APP_ICON, bytes);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_bottom_out);
        setContentView(R.layout.layout_app_detail);

        int heightPixels = getResources()
                .getDisplayMetrics().heightPixels;

        Window win = this.getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = (int) (heightPixels * 0.9);
        lp.gravity = Gravity.BOTTOM;
        win.setAttributes(lp);

        initViews();
        initData();

        if (!UserAgent.getInstance(AppDetailActivity.this).isVipUser()) {
            clearLocation();
            clearStepSetting();
        }

        if (mUserId != 0) {
            if (!UserAgent.getInstance(AppDetailActivity.this).isVipUser()) {
//                VipActivity.go(this);
                clearLocation();
                clearStepSetting();
                finish();
            }
        }

        SwipeBackLayout swipeBackLayout = new SwipeBackLayout(this);
        swipeBackLayout.attachToActivity(this);
        swipeBackLayout.setDirectionMode(SwipeBackLayout.FROM_TOP);
        swipeBackLayout.setMaskAlpha(0);
    }

    private void initViews() {
        TextView tvAppName = findViewById(R.id.detail_tv_app_name);

        ImageView ivAppIcon = findViewById(R.id.detail_iv_icon);

        Intent intent = getIntent();
        String appName = intent.getStringExtra(NAME_ARGUMENT);
        tvAppName.setText(appName);

        byte[] iconBytes = intent.getByteArrayExtra(APP_ICON);
        Bitmap bitmap = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length);
        ivAppIcon.setImageBitmap(bitmap);

        RecyclerView recyclerView = findViewById(R.id.plugin_recycler_view);
        recyclerView
                .setLayoutManager(new StaggeredGridLayoutManager(1, OrientationHelper.VERTICAL));
        recyclerView.addItemDecoration(new ItemOffsetDecoration(VUiKit.dpToPx(getContext(), 10)));
        mAdapter = new AppPluginAdapter(this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new AppPluginAdapter.OnItemOperateListener() {
            @Override
            public void onItemClick(PluginInfo info, int position) {
                mClickInfo = info;
//                if (TextUtils.equals(info.name, getString(R.string.create_shortcut))) {
//                    handleCreateShortcut();
//                    return;
//                }

                gotoFunction();
            }

            @Override
            public void onItemSwitch(PluginInfo pluginInfo, boolean isOpen) {
                if (TextUtils.equals(pluginInfo.name, getString(R.string.plugin_location))) {
                    simLocationOpen = isOpen;
                }
            }
        });

        ConstraintLayout btnLaunch = findViewById(R.id.detail_layout_launch);
        btnLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchApp();
            }
        });
    }

    private void gotoFunction() {
        if (mClickInfo == null) {
            return;
        }
//        if (!UserAgent.getInstance(AppDetailActivity.this).isVipUser()) {
////            VipActivity.go(this);
//            VipTipsDialog vipTipsDialog = new VipTipsDialog(this);
//            vipTipsDialog.show();
//            clearLocation();
//            clearStepSetting();
//        } else
        if (TextUtils.equals(mClickInfo.name, getString(R.string.plugin_no_mark))) {
            PluginTiktokActivity.launchTikTokSetting(this);
            StatAgent.onEvent(this, UmengStat.PLUGIN_CLICK, "name",
                    getString(R.string.plugin_no_mark));
        } else if (TextUtils.equals(mClickInfo.name, getString(R.string.plugin_location))) {
            gotoLocationSetting();
            StatAgent.onEvent(this, UmengStat.PLUGIN_CLICK, "name",
                    getString(R.string.plugin_location));
        } else if (TextUtils.equals(mClickInfo.name, getString(R.string.menu_mock_phone))) {
            gotoDeviceSetting();
            StatAgent.onEvent(this, UmengStat.PLUGIN_CLICK, "name",
                    getString(R.string.menu_mock_phone));
        } else if (TextUtils.equals(mClickInfo.name, getString(R.string.plugin_step_number))) {
            PluginStepActivity.start(this, mPkg);
            StatAgent.onEvent(this, UmengStat.PLUGIN_CLICK, "name",
                    getString(R.string.plugin_step_number));
        }
    }

    private void handleCreateShortcut() {
        new ShortcutDialog(this, mUserId, mPkg, null).show();
        StatAgent.onEvent(this, UmengStat.PLUGIN_CLICK, "name",
                getString(R.string.create_shortcut));
    }

    private void initData() {
        Intent intent = getIntent();
        mPkg = intent.getStringExtra(PKG_NAME_ARGUMENT);
        mName = intent.getStringExtra(NAME_ARGUMENT);
        mUserId = intent.getIntExtra(KEY_USER, 0);
        mAppId = intent.getIntExtra(KEY_APP_ID, -1);
        fakeAppInfo = (FakeAppInfo) intent.getSerializableExtra(FAKE_INFO);
        initPluginList();
        initDualApp(false);
        StatAgent.onEvent(this, UmengStat.DETAIL_ENTER, "name", mName);
        StatAgent.onEvent(this, UmengStat.DETAIL_ENTER, "package", mPkg);
        simLocationOpen = UserAgent.getInstance(this).isVipUser();
    }

    private void gotoDeviceSetting() {
        VUserInfo userInfo = VUserManager.get().getUserInfo(mUserId);
        if (userInfo != null) {
            DeviceData deviceData = new DeviceData(getContext(), null, userInfo.id);
            deviceData.name = userInfo.name;
            DeviceDetailActivity.open(this, deviceData, 0, DeviceDetailActivity.REQUEST_CODE);
        } else {
            startActivityForResult(new Intent(this, DeviceDetailActivity.class),
                    DeviceDetailActivity.REQUEST_CODE);
        }
    }

    private void gotoLocationSetting() {
        if (!PermissionCompat.checkPermissions(
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                false)) {
            PermissionCompat.startRequestPermissions(this, false,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    (requestCode, permissions, grantResults) -> {
                        boolean result = PermissionCompat.isRequestGranted(grantResults);
                        if (result) {
                            new Handler(getMainLooper()).post(this::goChooseBDLocationActivity);
                        }
                        return result;
                    });
        } else {
            goChooseBDLocationActivity();
        }
    }

    private void goChooseBDLocationActivity() {
        Intent intent = new Intent(this, ChooseBDLocationActivity.class);
        intent.putExtra(VCommends.EXTRA_PACKAGE, mPkg);
        intent.putExtra(VCommends.EXTRA_USERID, mUserId);
        intent.putExtra(VCommends.EXTRA_NAME, mName);
        startActivityForResult(intent, ChooseBDLocationActivity.REQUEST_CODE);
    }

    private void clearLocation() {
        try {
            VirtualLocationManager.get().setMode(mUserId, mPkg, VirtualLocationManager.MODE_CLOSE);
        } catch (Exception e) {
        }
    }

    private void clearStepSetting() {
        AppSharePref.getInstance(this).putInt(mPkg + "_stepTimes", 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ChooseBDLocationActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
//                launchApp();
                simLocationData = data.getParcelableExtra(ChooseBDLocationActivity.LOCATION_DATA);
                String curSimLocationAddress = data.getStringExtra(ChooseBDLocationActivity.LOCATION_ADDRESS);
                mAdapter.updateAddress(curSimLocationAddress);
                insertMockLocation(curSimLocationAddress);
            }
        } else if (requestCode == PluginStepActivity.REQUEST_CODE) {
            //
        } else if (requestCode == DeviceDetailActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String device = data.getStringExtra("device");
                mAdapter.updateDeviceInfo(device);
            }
            //
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void insertMockLocation(String curSimLocationAddress) {
        if (fakeAppInfo == null) {
            fakeAppInfo = new FakeAppInfo();
        } else {
            fakeAppInfo.setId(fakeAppInfo.getId());
        }
        fakeAppInfo.setAppId(Integer.parseInt(mAppId + "" + mUserId));
        if (!TextUtils.isEmpty(curSimLocationAddress)) {
            fakeAppInfo.setMockLocation(curSimLocationAddress);
        }
        FakeAppUtils.insert(fakeAppInfo);
    }

    /**
     * 初始化分身
     *
     * @param launch 初始完是否立即启动
     */
    private void initDualApp(boolean launch) {
        VUiKit.defer().when(() -> {
            mInstalling = true;
            try {
                InstallHelper.installPackage(AppDetailActivity.this, mPkg);
            } catch (PackageManager.NameNotFoundException ignored) {

            }
            mIntent = VCore.get().getLaunchIntent(mPkg, 0);
            if (mIntent == null) {
                mInstalling = false;
                return;
            }
            VCore.get().setUiCallback(mIntent, mUiCallback);
            if (launch) {
                VActivityManager.get().startActivity(mIntent, mUserId);
                mLaunchTimestamp = System.currentTimeMillis();
            }
            mInstalling = false;
        }).done(result -> {
        }).fail((e) -> {
            e.printStackTrace();
            mInstalling = false;
        });
    }

    private void initPluginList() {
        mAdapter.clear();
        if (UserAgent.getInstance(this).isVirtualLocationOn()) {
            PluginInfo pluginInfo = new PluginInfo(R.drawable.icon_sim_location, R.color.white,
                    getString(R.string.plugin_location), getString(R.string.location_des), true);
            if (UserAgent.getInstance(this).isVipUser()) {
                pluginInfo.isOpen = true;
            }
            mAdapter.add(pluginInfo);
        }

        mAdapter.add(new PluginInfo(R.drawable.icon_sim_device, R.color.white,
                getString(R.string.menu_mock_phone), getString(R.string.mock_phone_des)));

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (UserAgent.getInstance(this).isVirtualLocationOn()) {
                mAdapter.add(new PluginInfo(R.drawable.ic_plugin_step, R.color.white,
                        getString(R.string.plugin_step_number),
                        getString(R.string.plugin_action_times)));
            }
        }

//        mAdapter.add(new PluginInfo(R.drawable.ic_plugin_shortcut, R.color.white,
//                getString(R.string.create_shortcut),
//                getString(R.string.create_shortcut_tip)));

        if ((TextUtils.equals(VConstant.DOU_YIN_PKG, mPkg)
                || TextUtils.equals(VConstant.TikTok_PKG, mPkg))
                || TextUtils.equals(VConstant.TikTok_PKG2, mPkg)
                || TextUtils.equals(VConstant.TikTok_PKG3, mPkg)) {
            mAdapter.add(new PluginInfo(R.drawable.ic_plugin_download, R.color.white,
                    getString(R.string.plugin_no_mark), getString(R.string.nomark_des)));
        }
        if (fakeAppInfo != null) {
            if (!TextUtils.isEmpty(fakeAppInfo.getMockLocation())) {
                mAdapter.updateAddress(fakeAppInfo.getMockLocation());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchApp() {
        if (simLocationOpen) {
            if (simLocationData != null) {
                VCore.get().killApp(mPkg, mUserId);
                VirtualLocationManager.get()
                        .setMode(mUserId, mPkg, VirtualLocationManager.MODE_USE_SELF);
                // VCell vCell = new VCell();
                // vCell.mcc = 460;
                // vCell.lac = 4301;
                // vCell.cid = 20986;
                // VirtualLocationManager.get().setCell(mCurUserId, mCurPkg, vCell);
                VirtualLocationManager.get().setLocation(mUserId, mPkg, simLocationData.location);
            }
        } else {
            clearLocation();
        }
        launchAppInner();
    }

    private void launchAppInner() {
        boolean isBit64 = Bit64Utils.isRunOn64BitProcess(mPkg);
        if (isBit64) {
            if (!VCore.get().is64BitEngineInstalled()) {
                InstallHelper.install64Bit(this);
                return;
            } else if (CommonUtil.shouldUpdate64BitApk()) {
                InstallHelper.install64Bit(this);
                return;
            }
        }

        if (!CommonUtil.isAppInstalled(AppDetailActivity.this, mPkg)) {
            CommonUtil.launchAppMarket(mPkg, "", mName);
        } else if (TextUtils.equals(VConstant.PUBG_MOBILE_PACKAGE_NAME, mPkg)
                || TextUtils.equals(VConstant.PUBG2_MOBILE_PACKAGE_NAME, mPkg)) {//PUBG
            CommonUtil.startAppByPkgName(this, mPkg);
        } else {
            if (mLoadingDialog == null) {
                mLoadingDialog = new LoadingBottomDialog(this, mName);
                mLoadingDialog.setCanceledOnTouchOutside(false);
            }
            mLoadingDialog.setMessage(mName);
            mLoadingDialog.show();
            if (mIntent != null) {
                //已启动过，第二次就不展示loading框
                VUiKit.defer().when(() -> {
                    VActivityManager.get().startActivity(mIntent, mUserId);
                }).fail(result -> {
                    String localizedMessage = result.getLocalizedMessage();
                    Log.i("--------------", "message = " + localizedMessage);
                }).done(result -> {
                    GlobalData.setLaunchedApp(mPkg);
                    mLaunchTimestamp = System.currentTimeMillis();
                });
            } else if (mInstalling) {
                mHandler.sendEmptyMessageDelayed(MSG_WAIT_INSTALL, 1000);
            } else {
                initDualApp(true);
            }
        }

        StatAgent.onEvent(this, UmengStat.DUAL_LAUNCH, "name", mName);
        if (isBit64) {
            StatAgent.onEvent(this, UmengStat.DUAL_LAUNCH_64, "name", mName);
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WAIT_INSTALL:
                    if (mInstalling && mIntent == null) {
                        mHandler.sendEmptyMessageDelayed(MSG_WAIT_INSTALL, 500);
                    } else {
                        VUiKit.defer().when(() -> {
                            VActivityManager.get().startActivity(mIntent, mUserId);
                        }).done(unit -> {
                            mLaunchTimestamp = System.currentTimeMillis();
                        });
                    }
                    break;

            }
            return false;
        }
    });

    private final VCore.UiCallback mUiCallback = new VCore.UiCallback() {

        @Override
        public void onAppOpened(String packageName, int userId) {
            AndroidSchedulers.mainThread().scheduleDirect(() -> {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                    launchAppFinish = true;
                }
            });
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        //双开微信时，Activity access有时为null, mUiCallback 取消不了
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            launchAppFinish = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }

    }

    //加载分身完毕
    private boolean launchAppFinish = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (launchAppFinish) {
            finish();
        }
    }
}
