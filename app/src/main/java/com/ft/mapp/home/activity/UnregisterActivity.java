package com.ft.mapp.home.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.ft.mapp.R;
import com.ft.mapp.dialog.LoadingDialog;
import com.ft.mapp.dialog.UnregisterTipsDialog;
import com.ft.mapp.listener.OnDialogListener;
import com.jaeger.library.StatusBarUtil;
import com.xqb.user.net.engine.ApiServiceDelegate;
import com.xqb.user.net.lisenter.ApiCallback;

public class UnregisterActivity extends Activity {

    public static void start(Context context){
        Intent intent = new Intent(context, UnregisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unregister);
        StatusBarUtil.setTransparent(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.unregister_layout_iv_back:
            case R.id.unregister_tv_cancel:
                finish();
                break;
            case R.id.unregister_tv_ok:
                showTipsDialog();
                break;
        }
    }

    private void showTipsDialog() {
        UnregisterTipsDialog unregisterTipsDialog = new UnregisterTipsDialog(this);
        unregisterTipsDialog.setOnDialogListener(new OnDialogListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onOk() {
                unregister();
            }
        });
        unregisterTipsDialog.show();
    }

    private void unregister() {
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        new ApiServiceDelegate(this).unRegister(new ApiCallback() {
            @Override
            public void onSuccess() {
                loadingDialog.dismiss();
                finish();
            }

            @Override
            public void onFail(String errorMsg) {
                loadingDialog.dismiss();
            }

        });
    }
}
