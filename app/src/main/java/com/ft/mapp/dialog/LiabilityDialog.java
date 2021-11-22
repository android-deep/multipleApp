package com.ft.mapp.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ft.mapp.R;
import com.ft.mapp.home.PrivacyPolicyActivity;
import com.ft.mapp.home.UserProtocolActivity;
import com.ft.mapp.utils.AppSharePref;
import com.ft.mapp.utils.ToastUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;


public class LiabilityDialog extends AppCompatDialog {

    private TextView mSubmitTv;
    private Context mContext;
    private CheckBox checkBox;

    public LiabilityDialog(@NonNull Context context) {
        this(context, R.style.VBDialogTheme);
        initViews(context);
    }

    public LiabilityDialog(@NonNull Context context, int theme) {
        super(context, theme);
        initViews(context);
        setCanceledOnTouchOutside(false);
        this.mContext = context;

        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    private void initViews(Context context) {
        setContentView(R.layout.layout_liability);
        findViewById(R.id.dont_agree_tv).setOnClickListener(v -> dismiss());
        mSubmitTv = findViewById(R.id.agree_tv);
        mSubmitTv.setOnClickListener(v -> {
            if (checkBox.isChecked()) {
                AppSharePref.getInstance(mContext).putBoolean(AppSharePref.KEY_AGREE_POLICY, true);
                dismiss();
            }else{
                animTips();
            }
        });
        checkBox = findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            mSubmitTv.setEnabled(isChecked);
        });

        findViewById(R.id.privacy_tv).setOnClickListener(v -> {
            Intent intent1 = new Intent(mContext, PrivacyPolicyActivity.class);
            intent1.putExtra("action", mContext.getString(R.string.service_privacy_policy));
            mContext.startActivity(intent1);
        });

        findViewById(R.id.protocol_tv).setOnClickListener(v -> {
            Intent intent1 = new Intent(mContext, UserProtocolActivity.class);
            intent1.putExtra("action", mContext.getString(R.string.service_use_policy));
            mContext.startActivity(intent1);
        });
    }

    private void animTips() {
        ToastUtil.show(getContext(),"请先阅读上述协议");
        TranslateAnimation animation = new TranslateAnimation(20f,-20f,0,0);
        animation.setDuration(50);
        animation.setRepeatCount(4);
        animation.setRepeatMode(Animation.REVERSE);
        checkBox.startAnimation(animation);
    }

}
