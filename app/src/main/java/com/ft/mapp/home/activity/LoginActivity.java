package com.ft.mapp.home.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ft.mapp.R;
import com.ft.mapp.abs.ui.VActivity;
import com.ft.mapp.home.PrivacyPolicyActivity;
import com.ft.mapp.utils.CommonUtil;
import com.ft.mapp.utils.ToastUtil;
import com.google.gson.Gson;
import com.ipaynow.plugin.view.IpaynowLoading;
import com.jaeger.library.StatusBarUtil;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.xqb.user.bean.ThirdLoginResp;
import com.xqb.user.net.engine.ApiServiceDelegate;
import com.xqb.user.net.lisenter.ApiCallback;
import com.xqb.user.util.GsonUtil;
import com.xqb.user.util.UserSharePref;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Map;


public class LoginActivity extends VActivity implements View.OnClickListener {

    private EditText mNameEt;
    private EditText mPwdEt;
    private TextView mSubmitTv;
    private ImageView btnWx;
    private ConstraintLayout mLoginLayout;
    private EditText mNameEt1;
    private EditText mPwdEt1;
    private EditText mPwd2Et;
    private CheckBox cbPrivacy,cbLoginPrivacy;
    private LinearLayout mRegisterLayout;
    private TextView mRegisterTv;
    private TextView tvToLogin;
    private TextView tvPolicy,tvLoginPolicy;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.colorAccent));
        setContentView(R.layout.activity_login);
        initViews();
    }

    public void initViews() {
        mLoginLayout = findViewById(R.id.layout_login);
        mNameEt = findViewById(R.id.login_name_et);
        mPwdEt = findViewById(R.id.login_pwd_et);
        mSubmitTv = findViewById(R.id.login_submit_tv);
        mRegisterLayout = findViewById(R.id.layout_register);
        cbLoginPrivacy = findViewById(R.id.login_privacy_cb);
        mSubmitTv.setOnClickListener(this);

        btnWx = findViewById(R.id.login_iv_wx);
        btnWx.setOnClickListener(this);

        String toRegister = getString(R.string.no_register);
        SpannableStringBuilder builder = new SpannableStringBuilder(toRegister);
        builder.setSpan(new TextClick(this::gotoRegister), toRegister.length() - 2, toRegister.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mRegisterTv = findViewById(R.id.login_register_tv);
        mRegisterTv.setMovementMethod(LinkMovementMethod.getInstance());
        mRegisterTv.setText(builder);

        String toLogin = getString(R.string.to_login);
        SpannableStringBuilder builderLogin = new SpannableStringBuilder(toLogin);
        builderLogin.setSpan(new TextClick(this::gotoLogin), toLogin.length() - 2, toLogin.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvToLogin = findViewById(R.id.register_tv_tologin);
        tvToLogin.setMovementMethod(LinkMovementMethod.getInstance());
        tvToLogin.setText(builderLogin);

        String toPolicy = getString(R.string.to_policy);
        SpannableStringBuilder builderPolicy = new SpannableStringBuilder(toPolicy);
        builderPolicy.setSpan(new TextClick(this::gotoPolicy), toPolicy.length() - 15, toPolicy.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPolicy = findViewById(R.id.register_tv_policy);
        tvPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        tvPolicy.setText(builderPolicy);

        String toLoginPolicy = getString(R.string.to_login_policy);
        SpannableStringBuilder builderLoginPolicy = new SpannableStringBuilder(toLoginPolicy);
        builderLoginPolicy.setSpan(new TextClick(this::gotoPolicy), toPolicy.length() - 15, toPolicy.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLoginPolicy = findViewById(R.id.login_tv_policy);
        tvLoginPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        tvLoginPolicy.setText(builderLoginPolicy);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_submit_tv) {
            login();
        } else if (v.getId() == R.id.register_submit_tv) {
            requestResister();
        } else if (v.getId() == R.id.login_iv_wx) {
            thirdPartLogin(SHARE_MEDIA.WEIXIN);
        }
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, LoginActivity.class);
        context.startActivity(starter);
    }

    private void login() {
        if (!cbLoginPrivacy.isChecked()) {
            ToastUtil.show(this, "请先同意相关协议");
            return;
        }
        String userName = mNameEt.getText().toString().trim();
        if (userName.length() < 6) {
            ToastUtil.show(this, getString(R.string.input_correct_type));
            return;
        }
        if (!CommonUtil.checkMobile(userName)
                && !CommonUtil.checkEmail(userName)) {
            ToastUtil.show(this, getString(R.string.input_email));
            return;
        }
        String pwdString = mPwdEt.getText().toString().trim();
        if (TextUtils.isEmpty(pwdString)) {
            ToastUtil.show(this, getString(R.string.input_pwd));
            return;
        }

        showLoading("登录中");
        pwdString = CommonUtil.md5(pwdString);
        new ApiServiceDelegate(this).login(userName, pwdString, new ApiCallback() {
            @Override
            public void onSuccess() {
                UserSharePref.getInstance(LoginActivity.this).putBoolean(UserSharePref.KEY_LOGINED, true);
                ToastUtil.show(LoginActivity.this, R.string.login_success);
                setResult(Activity.RESULT_OK);
                closeLoading();
                finish();
            }

            @Override
            public void onFail(String msg) {
                closeLoading();
                String errorMsg;
                if (TextUtils.isEmpty(msg)) {
                    errorMsg = getString(R.string.login_fail);
                } else {
                    errorMsg = msg;
                }
                ToastUtil.show(LoginActivity.this, errorMsg);
            }
        });

    }

    private class TextClick extends ClickableSpan {

        public Runnable runnable;

        public TextClick(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void onClick(View widget) {
            if (runnable != null) {
                runnable.run();
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.dot_blue));
            ds.setUnderlineText(true);
        }
    }

    private void gotoRegister() {
        mLoginLayout.setVisibility(View.GONE);
        if (mRegisterLayout == null) {
            mRegisterLayout = findViewById(R.id.layout_register);
        }

        mRegisterLayout.setVisibility(View.VISIBLE);
        cbPrivacy = findViewById(R.id.register_privacy_cb);
        mNameEt1 = findViewById(R.id.register_name_et);
        mPwdEt1 = findViewById(R.id.register_pwd_et);
        mPwd2Et = findViewById(R.id.register_pwd2_et);

        findViewById(R.id.register_submit_tv).setOnClickListener(this);
    }

    private void showLoading(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        if (!progressDialog.isShowing()) {
            progressDialog.setMessage(msg);
            progressDialog.show();
        }
    }

    private void closeLoading(){
        if (progressDialog!=null){
            progressDialog.cancel();
        }
    }

    private void gotoLogin() {
        mLoginLayout.setVisibility(View.VISIBLE);
        mRegisterLayout.setVisibility(View.GONE);
    }

    private void gotoPolicy() {
        Intent intent1 = new Intent(this, PrivacyPolicyActivity.class);
        intent1.putExtra("action", getString(R.string.service_privacy_policy));
        startActivity(intent1);
    }

    private void requestResister() {
        String userName = mNameEt1.getText().toString();
        if (!CommonUtil.checkMobile(userName)
                && !CommonUtil.checkEmail(userName)) {
            ToastUtil.show(this, getString(R.string.input_email));
            return;
        }
        if (!cbPrivacy.isChecked()) {
            ToastUtil.show(this, "请先同意相关协议");
            return;
        }
        String pwd = mPwdEt1.getText().toString();
        String pwd2 = mPwd2Et.getText().toString();
        if (pwd.length() < 6 || pwd.length() > 12) {
            ToastUtil.show(this, getString(R.string.input_pwd_limit));
            return;
        }
        if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd2)) {
            ToastUtil.show(this, getString(R.string.input_pwd));
            return;
        }
        if (!TextUtils.equals(pwd, pwd2)) {
            ToastUtil.show(this, R.string.pwd_inconformity);
            return;
        }

        pwd = CommonUtil.md5(pwd);
        new ApiServiceDelegate(this).register(userName, pwd, new ApiCallback() {
            @Override
            public void onSuccess() {
                mRegisterLayout.setVisibility(View.GONE);
                mRegisterTv.setVisibility(View.GONE);
                mLoginLayout.setVisibility(View.VISIBLE);
                ToastUtil.show(LoginActivity.this, R.string.register_success);
                finish();
            }

            @Override
            public void onFail(String errorMsg) {
                ToastUtil.show(LoginActivity.this, errorMsg);
            }
        });

    }

    private void thirdPartLogin(SHARE_MEDIA shareMedia) {
        if (!cbLoginPrivacy.isChecked()) {
            ToastUtil.show(this, "请先同意相关协议");
            return;
        }
        showLoading("登录中");
        UMShareAPI.get(this).getPlatformInfo(this, shareMedia, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
//                StringBuilder sb = new StringBuilder();
//                for (Map.Entry<String, String> entry : map.entrySet()) {
//                    sb.append(entry.getKey()).append(":").append(entry.getValue()).append("\n");
//                }
                ThirdLoginResp thirdLoginResp = GsonUtil.gson2Bean(GsonUtil.getGsonString(map), ThirdLoginResp.class);
                assert thirdLoginResp != null;
                bindThirdInfo(1, thirdLoginResp);
//                ToastUtil.show(LoginActivity.this, "登录成功");
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                ToastUtil.show(LoginActivity.this, "登录异常_" + i);
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                ToastUtil.show(LoginActivity.this, "登录取消");
            }
        });
    }

    private void bindThirdInfo(Integer loginType, ThirdLoginResp thirdLoginResp) {
        new ApiServiceDelegate(this).thirdLogin(thirdLoginResp.getName(), thirdLoginResp.getGender(), thirdLoginResp.getIconurl(), loginType, thirdLoginResp.getUid(), new ApiCallback() {
            @Override
            public void onSuccess() {
                ToastUtil.show(LoginActivity.this, "登录成功");
                closeLoading();
                finish();
            }

            @Override
            public void onFail(String errorMsg) {
                closeLoading();
                ToastUtil.show(LoginActivity.this, errorMsg);
            }
        });
    }

}
