package com.ft.mapp.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.ft.mapp.R;
import com.ft.mapp.home.activity.VipActivity;
import com.ft.mapp.utils.ToastUtil;
import com.ft.mapp.utils.VipFunctionUtils;
import com.xqb.user.net.engine.UserAgent;

import java.util.concurrent.TimeUnit;

import jonathanfinerty.once.Once;

public class VipTipsDialog extends AppCompatDialog implements View.OnClickListener {

    private String currentType;

    private TextView mOkTv;
    private TextView mAdTv;
    private Context mContext;
    private TextView tvTitle;
    private TextView tvDesc;

    public VipTipsDialog(Context context, String functionType) {
        super(context, R.style.VBDialogTheme);
        setContentView(R.layout.dialog_vip_tips);
        this.mContext = context;
        this.currentType = functionType;
        initView();
        setVipFunction(functionType);
        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        setCanceledOnTouchOutside(false);
    }

    private void initView() {
        mOkTv = findViewById(R.id.vip_btn_ensure);
        mAdTv = findViewById(R.id.vip_btn_ad);
        tvTitle = findViewById(R.id.vip_tv_title);
        tvDesc = findViewById(R.id.vip_tv_desc);
        mOkTv.setOnClickListener(this);
        mAdTv.setOnClickListener(this);
    }

    private void setVipFunction(String functionType) {
        if (!UserAgent.getInstance(getContext()).isRewardOn()){
            commonTips();
            return;
        }
        switch (functionType) {
            case VipFunctionUtils.FUNCTION_MOCK_LOCATION:
                checkMockLocation();
                break;
            case VipFunctionUtils.FUNCTION_MOCK_DEVICE:
                checkMockDevice();
                break;
            case VipFunctionUtils.FUNCTION_ADD_LIMIT:
                checkAddLimit();
                break;
            default:
                commonTips();
                break;
        }
    }

    private void commonTips() {
        tvTitle.setText("温馨提示");
        tvDesc.setText("开通VIP即可无广告解锁所有功能");
        mAdTv.setVisibility(View.GONE);
    }

    private void checkMockLocation() {
        mAdTv.setText("观看广告，免费修改");
        tvTitle.setText("即将完成本次模拟定位");
        tvDesc.setText("开通VIP即可无广告解锁所有功能，您也可以观看广告免费试用本次功能");
        mAdTv.setVisibility(View.VISIBLE);
    }

    private void checkMockDevice() {
        mAdTv.setText("观看广告，免费修改");
        tvTitle.setText("即将完成本次机型模拟");
        tvDesc.setText("开通VIP即可无广告解锁所有功能，您也可以观看广告免费试用本次功能");
        mAdTv.setVisibility(View.VISIBLE);
    }

    private void checkAddLimit() {
        mAdTv.setText("观看广告，免费多开");
        tvTitle.setText("即将完成本次应用多开");
        tvDesc.setText("开通VIP即可无广告解锁所有功能，您也可以观看广告免费试用本次功能");
        mAdTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vip_btn_ad) {
            if (onVipAdListener != null) {
                onVipAdListener.adListener();
            }
//            if (!Once.beenDone(TimeUnit.HOURS, 1, currentType)) {
//                if (onVipAdListener != null) {
//                    onVipAdListener.adListener();
//                }
//            } else {
//                ToastUtil.show(getContext(), "广告正在冷却中，请稍候");
//            }

            dismiss();
        } else if (v.getId() == R.id.vip_btn_ensure) {
            VipActivity.go(mContext);
            dismiss();
        }
    }

    private OnVipAdListener onVipAdListener;

    public void setOnVipAdListener(OnVipAdListener onVipAdListener) {
        this.onVipAdListener = onVipAdListener;
    }

    public interface OnVipAdListener {
        void adListener();
    }

}
