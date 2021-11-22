package com.ft.mapp.home.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.ft.mapp.BrandConstant;
import com.ft.mapp.R;
import com.ft.mapp.abs.ui.VActivity;
import com.ft.mapp.ad.AdHelper;
import com.ft.mapp.ad.base.RewardAdListener;
import com.ft.mapp.ad.ttads.TTAdManagerHolder;
import com.ft.mapp.dialog.VipTipsDialog;
import com.ft.mapp.utils.ToastUtil;
import com.ft.mapp.utils.VipFunctionUtils;
import com.ft.mapp.widgets.BrandPopup;
import com.ft.mapp.home.adapters.ItemClickListener;
import com.ft.mapp.home.adapters.Section;
import com.ft.mapp.home.adapters.SectionedExpandableGridAdapter;
import com.ft.mapp.home.models.DeviceData;
import com.ft.mapp.home.models.BrandItem;
import com.ft.mapp.widgets.CommonDialog;
import com.fun.vbox.client.core.VCore;
import com.fun.vbox.client.ipc.VDeviceManager;
import com.fun.vbox.remote.VDeviceConfig;
import com.jaeger.library.StatusBarUtil;
import com.xqb.user.net.engine.UserAgent;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import jonathanfinerty.once.Once;

public class DeviceDetailActivity extends VActivity implements ItemClickListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener, BrandPopup.BrandChooseListener {

    public static final int REQUEST_CODE = 1002;
    private String curSimDeviceInfo;
    private BrandPopup brandDialog;
    private LinkedHashMap<String, ArrayList<BrandItem>> devicesData;
    private BrandItem currentBrandModel;
    private boolean mReward;
//    private TTRewardVideoAd currentAd;
//    private TTAdNative ttAdNative;
//    private TTAdNative.RewardVideoAdListener rewardVideoAdListener;

    public static void open(Activity activity, DeviceData data, int position, int requestCode) {
        Intent intent = new Intent(activity, DeviceDetailActivity.class);
        intent.putExtra("pkg", data.packageName);
        intent.putExtra("user", data.userId);
        intent.putExtra("pos", position);
        activity.startActivityForResult(intent, requestCode);
    }

    private String mPackageName;
    private int mUserId;
    private int mPosition;
    private VDeviceConfig mDeviceConfig;
    private RecyclerView rvModel;
    private TextView tvTitle;
    private TextView tvBrand;
    private RadioGroup rgHot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.colorPrimary));
        setResult(RESULT_CANCELED);
        setContentView(R.layout.activity_mock_device);

        tvTitle = findViewById(R.id.mock_device_tv_device);
        tvBrand = findViewById(R.id.mock_device_tv_brand);
        rgHot = findViewById(R.id.mock_device_rg_hot);
        ((RadioButton) findViewById(R.id.mock_device_rb_1)).setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.mock_device_rb_2)).setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.mock_device_rb_3)).setOnCheckedChangeListener(this);
//        rgHot.setOnCheckedChangeListener((group, checkedId) -> {
//
//        });
        findViewById(R.id.mock_device_iv_back).setOnClickListener(view -> finish());
        if (getIntent() != null) {
            mPackageName = getIntent().getStringExtra("pkg");
            mUserId = getIntent().getIntExtra("user", 0);
        }

        mDeviceConfig = VDeviceManager.get().getDeviceConfig(mUserId);
        rvModel = findViewById(R.id.mock_device_rv_model);
        rvModel.setLayoutManager(new GridLayoutManager(this, 3));

        currentBrandModel = initBrandItem();
        devicesData = BrandConstant.get(this);

        ArrayList<String> brandList = new ArrayList<>();

        for (LinkedHashMap.Entry<String, ArrayList<BrandItem>> entry :
                devicesData.entrySet()) {
            brandList.add(entry.getKey());
        }

        brandDialog = new BrandPopup(this, brandList);
        brandDialog.setBrandChooseListener(this);

        findViewById(R.id.device_save_tv).setOnClickListener(this);
        findViewById(R.id.mock_device_tv_reset).setOnClickListener(this);
        findViewById(R.id.mock_device_layout_brand).setOnClickListener(this);
        findViewById(R.id.mock_device_layout_custom).setOnClickListener(this);
//        ttAdNative = TTAdManagerHolder.get().createAdNative(this);
//        initAd();
    }

    private BrandItem initBrandItem() {
        String brand = mDeviceConfig.getProp("BRAND");
        if (TextUtils.isEmpty(brand)) {
            brand = Build.BRAND;
        }
        String model = mDeviceConfig.getProp("MODEL");
        if (TextUtils.isEmpty(model)) {
            model = Build.MODEL;
        }
        BrandItem curBrandItem;
        if (!TextUtils.isEmpty(brand) && !TextUtils.isEmpty(model)) {
            curSimDeviceInfo = brand + " " + model;
            tvTitle.setText(curSimDeviceInfo);
            curBrandItem = new BrandItem(brand, model);
        } else {
            curBrandItem = new BrandItem("", "");
        }
        return curBrandItem;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mPackageName = intent.getStringExtra("pkg");
        mUserId = intent.getIntExtra("user", 0);
        mPosition = intent.getIntExtra("pos", -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_device, menu);
        return true;
    }

    private void killApp() {
        if (TextUtils.isEmpty(mPackageName)) {
            VCore.get().killAllApps();
        } else {
            VCore.get().killApp(mPackageName, mUserId);
        }
    }

    private void reset() {
        mDeviceConfig.clear();
        mDeviceConfig.setProp("BRAND", Build.BRAND);
        mDeviceConfig.setProp("MODEL", Build.MODEL);
        mDeviceConfig.setProp("PRODUCT", Build.PRODUCT);
        mDeviceConfig.setProp("DEVICE", Build.DEVICE);
        mDeviceConfig.setProp("BOARD", Build.BOARD);
        mDeviceConfig.setProp("DISPLAY", Build.DISPLAY);
        mDeviceConfig.setProp("ID", Build.ID);
        mDeviceConfig.setProp("MANUFACTURER", Build.MANUFACTURER);
        mDeviceConfig.setProp("FINGERPRINT", Build.FINGERPRINT);

        rgHot.clearCheck();
        if (rvModel.getAdapter() != null) {
            ((SectionedExpandableGridAdapter) rvModel.getAdapter()).setSelectBrandItem(null);
        }
    }

    @Override
    public void itemClicked(BrandItem item) {
        curSimDeviceInfo = item.getBrand() + " " + item.getModel();
        tvTitle.setText(curSimDeviceInfo);
        currentBrandModel = item;
        rgHot.clearCheck();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            mDeviceConfig = VDeviceManager.get().getDeviceConfig(mUserId);
            initBrandItem();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void itemClicked(Section section) {
        section.isExpanded = !section.isExpanded;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.device_save_tv:
                if (!UserAgent.getInstance(this).isVipUser()) {
                    VipTipsDialog vipTipsDialog = new VipTipsDialog(this, VipFunctionUtils.FUNCTION_MOCK_DEVICE);
                    vipTipsDialog.setOnVipAdListener(new VipTipsDialog.OnVipAdListener() {
                        @Override
                        public void adListener() {
                            showAd();
                        }
                    });
                    vipTipsDialog.show();
                } else {
                    saveConfig();
                }
                break;
            case R.id.mock_device_tv_reset:
                new CommonDialog(this)
                        .setMessage(R.string.dlg_reset_device)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            if (mDeviceConfig != null) {
                                mDeviceConfig.enable = false;
                                reset();
                            }
                            VDeviceManager.get().updateDeviceConfig(mUserId, mDeviceConfig);
                            initBrandItem();

                            Intent intent = new Intent();
                            intent.putExtra("pkg", mPackageName);
                            intent.putExtra("user", mUserId);
                            intent.putExtra("pos", mPosition);
                            intent.putExtra("result", "reset");
                            intent.putExtra("device", curSimDeviceInfo);
                            setResult(RESULT_OK, intent);
                            killApp();
                        })
                        .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                        .show();
                break;
            case R.id.mock_device_layout_brand:
                brandDialog.showAsDropDown(v, 10, 15);
                break;
            case R.id.mock_device_layout_custom:
                if (UserAgent.getInstance(this).isVipUser()) {
                    DeviceCustomActivity.open(this, mPackageName, mUserId, mPosition, REQUEST_CODE);
                } else {
                    VipTipsDialog vipTipsDialog = new VipTipsDialog(this, VipFunctionUtils.FUNCTION_MOCK_CUSTOM_DEVICE);
                    vipTipsDialog.show();
                }
                break;
        }

    }

//    private void initAd() {
//        if (!UserAgent.getInstance(this).isRewardOn()) {
//            Log.i("----------ad status", "reward off");
//            return;
//        }
//        AdSlot adSlot = new AdSlot.Builder()
//                .setCodeId(TTAdManagerHolder.getVipRewardId())
//                .setSupportDeepLink(true)
//                .setOrientation(TTAdConstant.VERTICAL)
//                .build();
//        rewardVideoAdListener = new TTAdNative.RewardVideoAdListener() {
//            @Override
//            public void onError(int i, String s) {
//                Log.i("----------ad status", "reward onError = " + s);
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
//                        ttAdNative.loadRewardVideoAd(adSlot, rewardVideoAdListener);
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
//        ttAdNative.loadRewardVideoAd(adSlot, rewardVideoAdListener);
//    }

    private void showAd() {
        new AdHelper(this).showRewardAd(new RewardAdListener() {
            @Override
            public void onError(int code, String msg) {
                ToastUtil.show(DeviceDetailActivity.this, "广告还没准备好，请稍候再试");
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

//        if (currentAd != null) {
//            currentAd.showRewardVideoAd(this);
//        } else {
//            ToastUtil.show(this, "广告还没准备好，请稍候再试");
//            initAd();
//        }
    }

    private void saveConfig() {
        if (mDeviceConfig != null) {
            mDeviceConfig.enable = true;
        }
        if (currentBrandModel != null) {
            mDeviceConfig.setProp("BRAND", currentBrandModel.getBrand());
            mDeviceConfig.setProp("MODEL", currentBrandModel.getModel());
            mDeviceConfig.setProp("MANUFACTURER", currentBrandModel.getBrand());
            mDeviceConfig.setProp("PRODUCT", currentBrandModel.getBrand());
            mDeviceConfig.setProp("DEVICE", currentBrandModel.getBrand());
            mDeviceConfig.setProp("FINGERPRINT", deriveFingerprint(currentBrandModel.getBrand()));
        }
        VDeviceManager.get().updateDeviceConfig(mUserId, mDeviceConfig);
        Intent intent = new Intent();
        intent.putExtra("pkg", mPackageName);
        intent.putExtra("user", mUserId);
        intent.putExtra("pos", mPosition);
        intent.putExtra("result", "save");
        intent.putExtra("device", curSimDeviceInfo);
        setResult(RESULT_OK, intent);
        killApp();
        Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show();
        finish();
    }

    private String deriveFingerprint(String brand) {
        return brand + '/' +
                brand + '/' +
                brand + ':' +
                Build.VERSION.RELEASE + '/' +
                Build.ID + '/' +
                Build.VERSION.INCREMENTAL + ':' +
                Build.TYPE + '/' +
                Build.TAGS;
    }

    @Override
    public void onBrandChosen(String brand) {
        //当前品牌
        tvBrand.setText(brand);
        ArrayList<BrandItem> brandItems = devicesData.get(brand);
        SectionedExpandableGridAdapter sectionedExpandableGridAdapter = new SectionedExpandableGridAdapter(this, brandItems, this);
        rgHot.clearCheck();
        if (brandItems != null && brandItems.size() > 0) {
            sectionedExpandableGridAdapter.setSelectBrandItem(brandItems.get(0));
        } else {
            sectionedExpandableGridAdapter.setSelectBrandItem(null);
        }

        rvModel.setAdapter(sectionedExpandableGridAdapter);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked) {
            return;
        }
        BrandItem item = null;
        switch (buttonView.getId()) {
            case R.id.mock_device_rb_1:
                item = new BrandItem("iPhone", "11 Pro Max");
                break;
            case R.id.mock_device_rb_2:
                item = new BrandItem("华为", "Mate 30 Pro");
                break;
            case R.id.mock_device_rb_3:
                item = new BrandItem("三星", "Galaxy Note 10");
                break;
        }
        if (item != null) {
            currentBrandModel = item;
            curSimDeviceInfo = item.getBrand() + " " + item.getModel();
            tvTitle.setText(curSimDeviceInfo);
            if (rvModel.getAdapter() != null) {
                ((SectionedExpandableGridAdapter) rvModel.getAdapter()).setSelectBrandItem(null);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mReward) {
            saveConfig();
            mReward = false;
            VipFunctionUtils.markFunction(VipFunctionUtils.FUNCTION_MOCK_DEVICE);
        }
    }
}
